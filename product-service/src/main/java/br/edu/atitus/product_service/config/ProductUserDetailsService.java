package br.edu.atitus.product_service.config;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collections;

@Service
public class ProductUserDetailsService implements UserDetailsService {

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // ATENÇÃO: ISTO É UM MOCK SIMPLIFICADO.
        // Em um cenário real, você extrairia as roles do JWT (claims)
        // e criaria UserDetails com elas.
        // Para a validação JWT, o username é a parte principal.
        // As roles serão usadas para @PreAuthorize.
        // Se as roles não estiverem no JWT, este UserDetailsService precisaria de uma forma de obtê-las,
        // talvez fazendo uma chamada ao microsserviço de autenticação, o que é menos performático.

        // Para este exemplo, vamos assumir que o token JWT conterá o username e que
        // se o username existe, ele pode ter as roles necessárias para as rotas.
        // Você PODE incluir as roles como claims no JWT no seu microsserviço de autenticação.
        // Ex: { "sub": "email", "roles": ["ROLE_ADMIN", "ROLE_COMMON"] }

        // MOCK: Assumimos que qualquer usuário válido tem a role COMMON.
        // Para ADMIN, você precisaria que a role estivesse no JWT.
        if (username != null && !username.isEmpty()) {
            // Se o JWT contiver roles como claims, você as extrairia aqui.
            // Por enquanto, apenas um mock para permitir a validação JWT.
            return new User(username, "", Collections.singletonList(new SimpleGrantedAuthority("ROLE_COMMON")));
            // Se o JWT contiver "roles": ["ADMIN"], você passaria "ROLE_ADMIN" aqui.
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}