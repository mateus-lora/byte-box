package br.edu.atitus.identity_service.dto;

public class LoginResponseDTO {

    private UserLoginResponseDTO user;
    
    private String token;

    public LoginResponseDTO(UserLoginResponseDTO user, String token) {
        this.user = user;
        this.token = token;
    }

    public UserLoginResponseDTO getUser() {
        return user;
    }

    public String getToken() {
        return token;
    }

    public void setUser(UserLoginResponseDTO user) {
        this.user = user;
    }

    public void setToken(String token) {
        this.token = token;
    }
}