package JWT.JAVA.PROJECT.security.controller;

import java.time.Instant;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.LoginRequest;
import JWT.JAVA.PROJECT.security.dto.LoginResponse;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.repository.UserRepository;
import JWT.JAVA.PROJECT.security.service.AuthService;

@RestController
public class AuthController {
    
    @Autowired
    private AuthService authService;

    private final JwtEncoder JwtEncoder;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    public AuthController(JwtEncoder jwtEncoder, UserRepository userRepository,
    BCryptPasswordEncoder bCryptPasswordEncoder) {
        JwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
    }

    @PostMapping("/register")
    public ResponseEntity<HttpResponse> newUser(@RequestBody CreateUser dto){

        if (!dto.anyFieldIsNull().isEmpty()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Campos faltantes: " + dto.anyFieldIsNull()));
        }

        return authService.createUser(dto);

    } 

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest LoginRequest){
        var user = userRepository.findByUsername(LoginRequest.username());
        var expiresIn = 300L;
        var now = Instant.now();

        if (user.isEmpty() || !user.get().isLoginCorrect(LoginRequest, bCryptPasswordEncoder)) {
            throw new BadCredentialsException("User or password is invalid!");
        }

        var scope = user.get().getRoles().stream()
        .map(Role::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder().issuer("security").
        subject(user.get().getUserId().toString()).expiresAt(now.plusSeconds(expiresIn))
        .claim("scope", scope).issuedAt(now).build();

        var jwtValue = JwtEncoder.encode(JwtEncoderParameters.from(claims));

        return ResponseEntity.ok(new LoginResponse(jwtValue.getTokenValue(), expiresIn, HttpStatus.OK));
    }
}
