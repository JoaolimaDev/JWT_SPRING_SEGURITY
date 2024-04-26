package JWT.JAVA.PROJECT.security.dto;

import java.util.List;

import org.springframework.http.HttpStatus;

@SuppressWarnings("rawtypes")
public record HttpResponseList(HttpStatus httpStatus, List responseBody) {
    
}
