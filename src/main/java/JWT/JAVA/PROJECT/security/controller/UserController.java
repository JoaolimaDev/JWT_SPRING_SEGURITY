package JWT.JAVA.PROJECT.security.controller;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import JWT.JAVA.PROJECT.security.controller.dto.CreateUser;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.RoleRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;

@RestController
public class UserController {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public UserController(UserRepository userRepository, RoleRepository roleRepository, BCryptPasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/users")

    public ResponseEntity<Void> newUser(@RequestBody CreateUser dto){

        var basicRole = roleRepository.findByName(Role.Values.BASIC.name().toLowerCase());
        var userFromDb = userRepository.findByUsername(dto.username());

        if (userFromDb.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY);
        }

        var user = new User();
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUsername(dto.username());
        user.setRoles(Set.of(basicRole));

        userRepository.save(user);

        return ResponseEntity.ok().build();
    }

}
