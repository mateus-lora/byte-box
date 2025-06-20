package br.edu.atitus.identity_service.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.*;

import br.edu.atitus.identity_service.dto.LoginResponseDTO;
import br.edu.atitus.identity_service.dto.UserLoginDTO;
import br.edu.atitus.identity_service.dto.UserRegisterResponseDTO;
import br.edu.atitus.identity_service.dto.UserRegistrationDTO;
import br.edu.atitus.identity_service.model.User;
import br.edu.atitus.identity_service.service.UserService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/signup")
    public ResponseEntity<UserRegisterResponseDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            UserRegisterResponseDTO response = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }

    @PostMapping("/signup/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> registerAdmin(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        try {
            User admin = userService.registerAdmin(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(admin);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @PostMapping("/signin")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody UserLoginDTO loginDTO) {
        LoginResponseDTO response = userService.authenticateUserDto(loginDTO);

        return ResponseEntity.ok(response);
    }
}