package JWT.JAVA.PROJECT.security.dto;

import org.springframework.http.HttpStatus;

public record HttpResponse(HttpStatus httpStatus, String responseBody) {
    
}
