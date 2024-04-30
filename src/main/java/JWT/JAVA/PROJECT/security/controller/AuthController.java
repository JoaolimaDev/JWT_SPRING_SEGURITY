package JWT.JAVA.PROJECT.security.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.LoginRequest;
import JWT.JAVA.PROJECT.security.dto.LoginResponse;
import JWT.JAVA.PROJECT.security.service.AuthService;

@RestController
public class AuthController {
    
    @Autowired
    private AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> newUser(@RequestBody CreateUser dto){

        if (!dto.anyFieldIsNull().isEmpty()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Campos faltantes: " + dto.anyFieldIsNull()));
        }

        return authService.createUser(dto);

    } 

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest LoginRequest){

        if (!LoginRequest.anyFieldIsNull().isEmpty()) {
            return ResponseEntity.ok(new LoginResponse("Campos faltantes: " + LoginRequest.anyFieldIsNull(), 0L, HttpStatus.BAD_REQUEST));
        }
       
        return authService.loginRequest(LoginRequest);
    }
}
