package JWT.JAVA.PROJECT.security.dto;

import JWT.JAVA.PROJECT.security.config.NullableRecord;

public record LoginRequest(String username, String password) implements NullableRecord {
} 