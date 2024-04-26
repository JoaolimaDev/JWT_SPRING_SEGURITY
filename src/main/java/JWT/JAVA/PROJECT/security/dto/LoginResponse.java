package JWT.JAVA.PROJECT.security.dto;

import org.springframework.http.HttpStatus;

public record LoginResponse(String accessToken, Long expiresIn, HttpStatus httpStatus) {
    
}
