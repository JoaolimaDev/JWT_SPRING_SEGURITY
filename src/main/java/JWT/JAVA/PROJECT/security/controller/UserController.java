package JWT.JAVA.PROJECT.security.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import JWT.JAVA.PROJECT.security.config.ViewConfig;
import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.UpdateUser;
import JWT.JAVA.PROJECT.security.service.UserService;

@RestController
public class UserController {
    
    @Autowired
    public UserService userService;

    @Autowired
    public ViewConfig viewConfig;

    @PostMapping("/users")
    public ResponseEntity<Map<String, Object>> newUser(@RequestBody CreateUser dto){

        if (!dto.anyFieldIsNull().isEmpty()) {
           return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Campos faltantes: " + dto.anyFieldIsNull());
        }

        return userService.createUser(dto);

    } 

    @PutMapping("/users")
    public ResponseEntity<Map<String, Object>> updateUser(JwtAuthenticationToken token, @RequestBody UpdateUser dto){

        if (!dto.anyFieldIsNull().isEmpty()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Campos faltantes: " + dto.anyFieldIsNull());
        }
        
        return userService.updateUser(dto, token);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> listUsers(){
       
      return userService.listUsers();
    }

    @DeleteMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    public ResponseEntity<Map<String, Object>> deleteUser(JwtAuthenticationToken token){

        return userService.deleteUser(token);
    }

}
