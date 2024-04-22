package JWT.JAVA.PROJECT.security.repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import JWT.JAVA.PROJECT.security.model.User;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    
    @Query(value = "SELECT t.user_id, t.username, t.email FROM tb_user t", nativeQuery = true)
    List<Map<String, Object>> findAllUsers();
 
} 