package JWT.JAVA.PROJECT.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import JWT.JAVA.PROJECT.security.model.Tweet;

public interface TweetRepository extends JpaRepository<Tweet, Long>{

}
