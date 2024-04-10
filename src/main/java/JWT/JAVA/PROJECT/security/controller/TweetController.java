package JWT.JAVA.PROJECT.security.controller;

import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import JWT.JAVA.PROJECT.security.controller.dto.CreateTweetDto;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.Tweet;
import JWT.JAVA.PROJECT.security.repository.TweetRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;
import jakarta.annotation.Generated;

@RestController
public class TweetController {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;

    public TweetController(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    @PostMapping("/tweets")
    public ResponseEntity<Void> CreateTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());
        
        tweetRepository.save(tweet);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> DeletTweets(@PathVariable("id") Long tweetId, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = tweetRepository.findById(tweetId).orElseThrow( () -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        var isAdmin = user.get().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));


        if (isAdmin || tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            tweetRepository.deleteById(tweetId);
        }else{
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }


        return ResponseEntity.ok().build();
    }


    
    
}
