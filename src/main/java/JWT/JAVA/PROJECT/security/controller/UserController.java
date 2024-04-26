package JWT.JAVA.PROJECT.security.controller;

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

import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.HttpResponseList;
import JWT.JAVA.PROJECT.security.dto.UpdateUser;
import JWT.JAVA.PROJECT.security.service.UserService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class UserController {
    
    @Autowired
    public UserService userService;

    @PutMapping("/users")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponse> updateUser(JwtAuthenticationToken token, @RequestBody UpdateUser dto){

        if (!dto.anyFieldIsNull().isEmpty()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Campos faltantes: " + dto.anyFieldIsNull()));
        }
        
        return userService.updateUser(dto, token);
    }

    @GetMapping("/users")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponseList> listUsers(){
       
      return userService.listUsers();
    }

    @DeleteMapping("/users/{id}/")
    @PreAuthorize("hasAuthority('SCOPE_admin')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponse> deleteUser(@PathVariable("id") String userId, JwtAuthenticationToken token){

        return userService.deleteUser(userId, token);
    }

}
