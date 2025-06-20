package br.edu.atitus.identity_service.service;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import br.edu.atitus.identity_service.dto.UserLoginResponseDTO;

import br.edu.atitus.identity_service.dto.LoginResponseDTO;
import br.edu.atitus.identity_service.dto.UserLoginDTO;
import br.edu.atitus.identity_service.dto.UserRegisterResponseDTO;
import br.edu.atitus.identity_service.dto.UserRegistrationDTO;
import br.edu.atitus.identity_service.model.User;
import br.edu.atitus.identity_service.model.UserRole;
import br.edu.atitus.identity_service.repository.UserRepository;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService, AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.authenticationManager = authenticationManager;
    }

    public UserRegisterResponseDTO registerUser(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado!");
        }

        String encodedPassword = passwordEncoder.encode(registrationDTO.password());

        User newUser = new User(
                registrationDTO.name(),
                registrationDTO.email(),
                encodedPassword,
                UserRole.COMMON
        );

        User savedUser = userRepository.save(newUser);

        return new UserRegisterResponseDTO(savedUser);
    }
    
    public LoginResponseDTO authenticateUserDto(UserLoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
            );

            if (authentication.isAuthenticated()) {
                User user = userRepository.findByEmail(loginDTO.email())
                                          .orElseThrow(() -> new RuntimeException("Erro interno: Usuário autenticado não encontrado no banco de dados."));

                String jwt = jwtService.generateToken(user);

                UserLoginResponseDTO userResponse = new UserLoginResponseDTO(user);

                return new LoginResponseDTO(userResponse, jwt);

            } else {
                throw new RuntimeException("Credenciais inválidas.");
            }
        } catch (AuthenticationException e) {
            System.err.println("Falha na autenticação para o email: " + loginDTO.email() + " - Detalhe: " + e.getMessage());
            throw new RuntimeException("Usuário inexistente ou senha inválida.", e);
        }
    }
    public User registerAdmin(UserRegistrationDTO registrationDTO) {
        if (userRepository.findByEmail(registrationDTO.email()).isPresent()) {
            throw new RuntimeException("Email já cadastrado!");
        }
        String encodedPassword = passwordEncoder.encode(registrationDTO.password());
        User newAdmin = new User(
                registrationDTO.name(),
                registrationDTO.email(),
                encodedPassword,
                UserRole.ADMIN
        );
        return userRepository.save(newAdmin);
    }

    public String authenticateUser(UserLoginDTO loginDTO) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.email(), loginDTO.password())
            );

            if (authentication.isAuthenticated()) {
                User user = userRepository.findByEmail(loginDTO.email())
                                          .orElseThrow(() -> new RuntimeException("Usuário não encontrado após autenticação!"));
                return jwtService.generateToken(user);
            } else {
                throw new RuntimeException("Credenciais inválidas.");
            }
        } catch (AuthenticationException e) {
            System.err.println("DEBUG: UserService - Falha na autenticação para o email: " + loginDTO.email() + " - Mensagem detalhada: " + e.getMessage()); // Log 5
            e.printStackTrace();
            throw new RuntimeException("Usuário inexistente ou senha inválida.", e);
        }
    }
}