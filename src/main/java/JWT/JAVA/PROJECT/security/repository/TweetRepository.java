package JWT.JAVA.PROJECT.security.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import JWT.JAVA.PROJECT.security.model.Tweet;

public interface TweetRepository extends JpaRepository<Tweet, Long>{

    @Query(value = "SELECT t.tweet_id, t.content, t.timestamp FROM tb_tweets t", nativeQuery = true)
    List<Map<String, Object>> findTweetsWithoutUserData();
}
