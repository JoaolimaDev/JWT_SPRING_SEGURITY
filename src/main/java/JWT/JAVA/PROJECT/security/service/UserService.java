package JWT.JAVA.PROJECT.security.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import JWT.JAVA.PROJECT.security.config.PasswordConfig;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.HttpResponseList;
import JWT.JAVA.PROJECT.security.dto.UpdateUser;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.RoleRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;


@Service
public class UserService implements PasswordConfig {

    public RoleRepository roleRepository;
    public UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;


    public UserService(RoleRepository roleRepository, UserRepository userRepository, BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
    }

    
    public ResponseEntity<HttpResponse> updateUser(UpdateUser dto, JwtAuthenticationToken id){

        if (dto.role().contains("admin")) {
            
            return ResponseEntity.ok(new HttpResponse(HttpStatus.FORBIDDEN, "Role admin não é válida para este endpoint!"));
        }

        var whichRole = roleRepository.findByName(dto.role());
        var pwordVerify = passwordConfig(dto.password());
        var emailFromDb = userRepository.findByEmail(dto.email());
        var userFromDb = userRepository.findByUsername(dto.username());

        if (dto.email().isEmpty() || dto.password().isEmpty() || dto.username().isEmpty() || dto.role().isEmpty()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Preencha todos os Campos!"));
        }

        if (!whichRole.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Role : "+dto.role().toString()+" inválida!"));
        }

        if (userFromDb.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Usuário já cadastrado!"));
        }

        if (pwordVerify != null) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, pwordVerify));
        }

        if (emailFromDb.isPresent()) {
            return ResponseEntity.ok(new HttpResponse(HttpStatus.BAD_REQUEST, "Email já cadastrado!"));
        }

        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id.getName()));

        String userUpdate = optionalUser.map(user -> {
            Set<Role> roles = new HashSet<>(Set.of(whichRole.get()));
            user.setRoles(roles);
            user.setUsername(dto.username());
            user.setEmail(dto.email());
            user.setPassword(bCryptPasswordEncoder.encode(dto.password()));
            userRepository.save(user);
            return "Usuário " + dto.username() + " atualizado!";
        }).orElse("Usuário não encontrado!");
      

        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, userUpdate));

    }

    public ResponseEntity<HttpResponseList> listUsers(){

        return ResponseEntity.ok(new HttpResponseList(HttpStatus.OK, userRepository.findAllUsers()));
    }

    public ResponseEntity<HttpResponse> deleteUser(String id, JwtAuthenticationToken token){

        Optional<User> userOptional = userRepository.findById(UUID.fromString(id));

        String userDeleted = userOptional.map(user -> {

            var isAdmin = user.getRoles().toString().equals("admin");

            if (isAdmin) {
                userRepository.deleteById(UUID.fromString(id));
                return "Usuário deletado por admin!";
            }

            userRepository.deleteById(UUID.fromString(id));
            return "Usuário deletado!";

        }).orElse("Usuário inexistente!");

        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, userDeleted));
    }

}
