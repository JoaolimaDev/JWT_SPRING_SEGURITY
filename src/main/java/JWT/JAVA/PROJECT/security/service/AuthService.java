package JWT.JAVA.PROJECT.security.service;

import java.util.HashSet;
import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import JWT.JAVA.PROJECT.security.config.PasswordConfig;
import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.RoleRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;

@Service
public class AuthService implements PasswordConfig {

    public RoleRepository roleRepository;
    public UserRepository userRepository;
    public BCryptPasswordEncoder passwordEncoder;

    public AuthService(RoleRepository roleRepository, UserRepository userRepository,
     BCryptPasswordEncoder passwordEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
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
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUsername(dto.username().toString());
        user.setEmail(dto.email().toString());
        
        userRepository.save(user);

        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, "Usuário " + dto.username() + " cadastrado!"));

    }
    
}
