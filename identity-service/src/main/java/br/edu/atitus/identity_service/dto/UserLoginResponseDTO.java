package br.edu.atitus.identity_service.dto;

import br.edu.atitus.identity_service.model.UserRole;
import br.edu.atitus.identity_service.model.User;

public record UserLoginResponseDTO(
 Long id,
 String name,
 String email,
 UserRole type,
 String username,
 boolean enabled,
 boolean accountNonExpired,
 boolean accountNonLocked,
 boolean credentialsNonExpired
) {

	public UserLoginResponseDTO(User user) {
     this(
         user.getId(),
         user.getName(),
         user.getEmail(),
         user.getRole(),
         user.getUsername(),
         user.isEnabled(),
         user.isAccountNonExpired(),
         user.isAccountNonLocked(),
         user.isCredentialsNonExpired()
     );
 }
}