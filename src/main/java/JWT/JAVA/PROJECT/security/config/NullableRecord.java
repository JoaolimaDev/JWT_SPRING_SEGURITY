package JWT.JAVA.PROJECT.security.config;

import java.lang.reflect.Field;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public interface NullableRecord {
    default String anyFieldIsNull() {
        return Stream.of(getClass().getDeclaredFields())
        .filter(field -> {
            try {
                field.setAccessible(true);
                return field.get(this) == null;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }
        })
        .map(Field::getName)
        .collect(Collectors.joining(", "));
    }
}