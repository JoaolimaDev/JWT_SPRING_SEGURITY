package JWT.JAVA.PROJECT.security.service;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import org.springframework.security.oauth2.jwt.JwtEncoder;


import JWT.JAVA.PROJECT.security.config.PasswordConfig;
import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.LoginRequest;
import JWT.JAVA.PROJECT.security.dto.LoginResponse;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.RoleRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;

@Service
public class AuthService implements PasswordConfig{

    public RoleRepository roleRepository;
    public UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtEncoder JwtEncoder;
    

    public AuthService(RoleRepository roleRepository, UserRepository userRepository,BCryptPasswordEncoder bCryptPasswordEncoder, 
    JwtEncoder jwtEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        JwtEncoder = jwtEncoder;
    }

    public ResponseEntity<LoginResponse> loginRequest(LoginRequest LoginRequest){

        var user = userRepository.findByUsername(LoginRequest.username());
        var expiresIn = 300L;
        var now = Instant.now();

        if (user.isEmpty() || !user.get().isLoginCorrect(LoginRequest, bCryptPasswordEncoder)) {
            return ResponseEntity.ok(new LoginResponse("Usuário ou senha inválidos!", 0L, HttpStatus.BAD_REQUEST));
        }

        var scope = user.get().getRoles().stream()
        .map(Role::getName).collect(Collectors.joining(" "));

        var claims = JwtClaimsSet.builder().issuer("security").
        subject(user.get().getUserId().toString()).expiresAt(now.plusSeconds(expiresIn))
        .claim("scope", scope).issuedAt(now).build();

        var jwtValue = JwtEncoder.encode(JwtEncoderParameters.from(claims));

        return ResponseEntity.ok(new LoginResponse(jwtValue.getTokenValue(), expiresIn, HttpStatus.OK));
    }

    public ResponseEntity<HttpResponse> createUser(CreateUser dto){

        if (dto.role().contains("admin")) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.FORBIDDEN, "Role admin não é válida para este endpoint!"));
        }

        var whichRole = roleRepository.findByName(dto.role().toLowerCase());
        var userFromDb = userRepository.findByUsername(dto.username());
        var pwordVerify = passwordConfig(dto.password());
        var emailFromDb = userRepository.findByEmail(dto.email());

        if (dto.email().isEmpty() || dto.password().isEmpty() || dto.username().isEmpty() || dto.role().isEmpty()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Preencha todos os Campos!"));
        }
        
        if (userFromDb.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Usuário já cadastrado!"));
        }

        if (!whichRole.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Role : "+dto.role().toString()+" inválida!"));
        }

        if (emailFromDb.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Email já cadastrado!"));
        }

        if (pwordVerify != null) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, pwordVerify));
        }
        
        var user = new User();
        Set<Role> roles = new HashSet<>(Set.of(whichRole.get()));
        user.setRoles(roles);
        user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
        user.setUsername(dto.username().toString());
        user.setEmail(dto.email().toString());
        
        userRepository.save(user);

        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, "Usuário " + dto.username() + " cadastrado!"));

    }
    
}
