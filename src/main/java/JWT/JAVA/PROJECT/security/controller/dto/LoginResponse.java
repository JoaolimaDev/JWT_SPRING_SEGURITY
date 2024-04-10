package JWT.JAVA.PROJECT.security.controller.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
    
}
