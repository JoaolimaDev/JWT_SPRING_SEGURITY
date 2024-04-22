package JWT.JAVA.PROJECT.security.dto;

import JWT.JAVA.PROJECT.security.config.NullableRecord;

public record CreateUser(String username, String password, String email, String role) implements NullableRecord  {
    
}
