package JWT.JAVA.PROJECT.security.service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;


import JWT.JAVA.PROJECT.security.dto.CreateTweetDto;
import JWT.JAVA.PROJECT.security.model.Role;
import JWT.JAVA.PROJECT.security.model.Tweet;
import JWT.JAVA.PROJECT.security.repository.TweetRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;

@Service
public class TweetService {

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public void CreateTweet(CreateTweetDto dto, JwtAuthenticationToken token){
        
        var user = userRepository.findById(UUID.fromString(token.getName()));

        var tweet = new Tweet();
        tweet.setUser(user.get());
        tweet.setContent(dto.content());
        
        tweetRepository.save(tweet);
    }

    public boolean DeleteTweet(Long tweetId, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        Optional<Tweet> tweet = tweetRepository.findById(tweetId);

        var isAdmin = user.get().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase(Role.Values.ADMIN.name()));

        if (isAdmin || tweet.get().getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            
            tweetRepository.deleteById(tweetId);

            return true;
        }

        return false;

    }   

    public boolean UpdateTweets(CreateTweetDto dto, Long tweetId, JwtAuthenticationToken token){

        Optional<Tweet> optionalTweet = tweetRepository.findById(tweetId);

        if (!optionalTweet.get().getUser().getUserId().equals(UUID.fromString(token.getName()))) {
            
            return false;
        }

        boolean tweetUpdated = optionalTweet.map(tweet -> {
            tweet.setContent(dto.content());
            tweetRepository.save(tweet);
            return true; 
        }).orElse(false);
        
        return tweetUpdated;
    }

    @SuppressWarnings("rawtypes")
    public List GetTweets(){
        return tweetRepository.findTweetsWithoutUserData();
    }
   
}