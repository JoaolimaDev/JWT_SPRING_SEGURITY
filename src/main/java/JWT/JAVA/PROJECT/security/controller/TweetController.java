package JWT.JAVA.PROJECT.security.controller;

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
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.HttpResponseList;
import JWT.JAVA.PROJECT.security.service.TweetService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
public class TweetController {

    @Autowired
    private TweetService tweetService;


    @PostMapping("/tweets")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponse> CreateTweet(@RequestBody CreateTweetDto dto, JwtAuthenticationToken token){

       return tweetService.CreateTweet(dto, token);
    }

    @DeleteMapping("/tweets/{id}/")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponse> DeletTweets(@PathVariable("id") String tweetId, JwtAuthenticationToken token){

        return tweetService.DeleteTweet(tweetId, token);
    }

    @PutMapping("/tweets/{id}/")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponse>  UpdateTweets(@RequestBody CreateTweetDto dto, @PathVariable("id") String tweetId, JwtAuthenticationToken token){

        return tweetService.UpdateTweets(dto, tweetId, token);
    }

    @GetMapping("/tweets")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<HttpResponseList> GetTweets(){

        return tweetService.GetTweets();
        
    }

}
