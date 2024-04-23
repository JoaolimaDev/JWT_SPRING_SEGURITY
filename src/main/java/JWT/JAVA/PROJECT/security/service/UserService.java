package JWT.JAVA.PROJECT.security.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.regex.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import JWT.JAVA.PROJECT.security.config.ViewConfig;
import JWT.JAVA.PROJECT.security.dto.CreateUser;
import JWT.JAVA.PROJECT.security.dto.UpdateUser;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.RoleRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;


@Service
public class UserService {

    @Autowired
    private ViewConfig viewConfig;
    
    public RoleRepository roleRepository;
    public UserRepository userRepository;
    public BCryptPasswordEncoder passwordEncoder;

    public UserService(RoleRepository roleRepository, UserRepository userRepository,
    BCryptPasswordEncoder passwordEncoder) {

        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public String passwordConfig(String password){

        if (password.length() < 8) {
            return "A senha deve conter pelo menos 8 caracteres!";
        }

        if (!Pattern.compile("[A-Z]").matcher(password).find()) {
            return "A senha deve conter pelo menos uma letra maiúscula!";
        }

        if (!Pattern.compile("[a-z]").matcher(password).find()) {
            return "A senha deve conter pelo menos uma letra minúscula!";
        }
        
        if (!Pattern.compile("[0-9]").matcher(password).find()) {
            return "A senha deve conter pelo menos um número!";
        }

        if (!Pattern.compile("[^a-zA-Z0-9]").matcher(password).find()) {
            return "A senha deve conter pelo menos um caractere especial!";
        }

        return null;
    }

    public ResponseEntity<Map<String, Object>> createUser(CreateUser dto){

        if (dto.role().contains("admin")) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Role admin não é válida para este endpoint!");
        }

        var whichRole = roleRepository.findByName(dto.role().toLowerCase());
        var userFromDb = userRepository.findByUsername(dto.username());
        var pwordVerify = passwordConfig(dto.password());
        var emailFromDb = userRepository.findByEmail(dto.email());

        if (dto.email().isEmpty() || dto.password().isEmpty() || dto.username().isEmpty() || dto.role().isEmpty()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Preencha todos os Campos!");
        }
        
        if (userFromDb.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Usuário já cadastrado!");
        }

        if (!whichRole.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Role : "+dto.role().toString()+" inválida!");
        }

        if (emailFromDb.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Email já cadastrado!");
        }

        if (pwordVerify != null) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, pwordVerify);
        }
        
        var user = new User();
        Set<Role> roles = new HashSet<>(Set.of(whichRole.get()));
        user.setRoles(roles);
        user.setPassword(passwordEncoder.encode(dto.password()));
        user.setUsername(dto.username().toString());
        user.setEmail(dto.email().toString());
        
        userRepository.save(user);

        return viewConfig.ResponseEntity(HttpStatus.OK, "Usuário " + dto.username() + " cadastrado!");

    }

    public ResponseEntity<Map<String, Object>> updateUser(UpdateUser dto, JwtAuthenticationToken id){

        if (dto.role().contains("admin")) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Role admin não é válida para este endpoint!");
        }

        var whichRole = roleRepository.findByName(dto.role());
        var pwordVerify = passwordConfig(dto.password());
        var emailFromDb = userRepository.findByEmail(dto.email());
        var userFromDb = userRepository.findByUsername(dto.username());

        if (dto.email().isEmpty() || dto.password().isEmpty() || dto.username().isEmpty() || dto.role().isEmpty()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Preencha todos os Campos!");
        }

        if (!whichRole.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Role : "+dto.role().toString()+" inválida!");
        }

        if (userFromDb.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Usuário já cadastrado!");
        }

        if (pwordVerify != null) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, pwordVerify);
        }

        if (emailFromDb.isPresent()) {
            return viewConfig.ResponseEntity(HttpStatus.BAD_REQUEST, "Email já cadastrado!");
        }

        Optional<User> optionalUser = userRepository.findById(UUID.fromString(id.getName()));

        String userUpdate = optionalUser.map(user -> {
            Set<Role> roles = new HashSet<>(Set.of(whichRole.get()));
            user.setRoles(roles);
            user.setUsername(dto.username());
            user.setEmail(dto.email());
            user.setPassword(dto.password());
            userRepository.save(user);
            return "Usuário " + dto.username() + " atualizado!";
        }).orElse("Usuário não encontrado!");
      
        return viewConfig.ResponseEntity(HttpStatus.OK, userUpdate);
    }

    public ResponseEntity<Map<String, Object>> listUsers(){

        return viewConfig.ResponseEntityList(HttpStatus.OK, userRepository.findAllUsers());
    }

    public ResponseEntity<Map<String, Object>> deleteUser(JwtAuthenticationToken token){

        Optional<User> userOptional = userRepository.findById(UUID.fromString(token.getName()));

        String userDeleted = userOptional.map(user -> {

            var isAdmin = user.getRoles().toString().equals("admin");

            if (isAdmin) {
                userRepository.deleteById(user.getUserId());
                return "Usuário deletado por admin!";
            }

            userRepository.deleteById(user.getUserId());
            return "Usuário deletado!";

        }).orElse("Usuário inexistente!");

        return viewConfig.ResponseEntity(HttpStatus.OK, userDeleted);
    }

}
