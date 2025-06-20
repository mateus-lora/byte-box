package br.edu.atitus.identity_service.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import br.edu.atitus.identity_service.repository.UserRepository;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
	    System.out.println("DEBUG: UserDetailService - Tentando carregar usuário com email: " + email); // Log 1

	    return userRepository.findByEmail(email)
	            .orElseThrow(() -> {
	                System.err.println("DEBUG: UserDetailService - Usuário com email " + email + " NÃO encontrado."); // Log 2
	                return new UsernameNotFoundException("Usuário não encontrado: " + email);
	            });
	}
}