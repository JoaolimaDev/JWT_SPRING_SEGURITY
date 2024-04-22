package JWT.JAVA.PROJECT.security.dto;

public record LoginResponse(String accessToken, Long expiresIn) {
    
}
