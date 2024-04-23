package JWT.JAVA.PROJECT.security.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import JWT.JAVA.PROJECT.security.dto.CreateTweetDto;
import JWT.JAVA.PROJECT.security.service.TweetService;

@RestController
public class TweetController {

    @Autowired
    private TweetService tweetService;


    @PostMapping("/tweets")
    public ResponseEntity<Map<String, Object>> CreateTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token){

       return tweetService.CreateTweet(dto, token);
    }

    @DeleteMapping("/tweets/{id}/")
    public ResponseEntity<Map<String, Object>> DeletTweets(@PathVariable("id") String tweetId, JwtAuthenticationToken token){

        return tweetService.DeleteTweet(tweetId, token);
    }

    @PutMapping("/tweets/{id}/")
    public ResponseEntity<Map<String, Object>>  UpdateTweets(@RequestBody CreateTweetDto dto, @PathVariable("id") String tweetId, JwtAuthenticationToken token){

        return tweetService.UpdateTweets(dto, tweetId, token);
    }

    @GetMapping("/tweets")
    public ResponseEntity<Map<String, Object>> GetTweets(){

        return tweetService.GetTweets();
        
    }

}
