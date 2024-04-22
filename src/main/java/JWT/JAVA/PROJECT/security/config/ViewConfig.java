package JWT.JAVA.PROJECT.security.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class ViewConfig {
    
    private final Map<String, Object> responseMap;

    public ViewConfig() {
        this.responseMap = new HashMap<>();
    }

    public ResponseEntity<Map<String, Object>> ResponseEntity(HttpStatus httpStatus, String responseBody){
        
        responseMap.put("message", responseBody);
        responseMap.put("status", httpStatus.value());

        return ResponseEntity.status(httpStatus.value()).body(responseMap);
    }
 
    public ResponseEntity<Map<String, Object>> ResponseEntityList(HttpStatus httpStatus, @SuppressWarnings("rawtypes") List responseBody){
        
        responseMap.put("message", responseBody);
        responseMap.put("status", httpStatus.value());

        return ResponseEntity.status(httpStatus.value()).body(responseMap);
    }
}
