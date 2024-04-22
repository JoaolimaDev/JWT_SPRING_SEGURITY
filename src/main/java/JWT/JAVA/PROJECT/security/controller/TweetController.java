package JWT.JAVA.PROJECT.security.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<Void> CreateTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token){

        tweetService.CreateTweet(dto, token);

        return ResponseEntity.ok().build();
        
    }

    @DeleteMapping("/tweets/{id}")
    public ResponseEntity<Void> DeletTweets(@PathVariable("id") Long tweetId, JwtAuthenticationToken token){

        if (tweetService.DeleteTweet(tweetId, token)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @PutMapping("/tweets/{id}")
    public ResponseEntity<Void>  UpdateTweets(@RequestBody CreateTweetDto dto, @PathVariable("id") Long tweetId, JwtAuthenticationToken token){

        if (tweetService.UpdateTweets(dto, tweetId, token)) {
            return ResponseEntity.ok().build();
        }

        return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
    }

    @SuppressWarnings("rawtypes")
    @GetMapping("/tweets")
    public ResponseEntity<List> GetTweets(){

        return ResponseEntity.ok(tweetService.GetTweets());
        
    }

}
