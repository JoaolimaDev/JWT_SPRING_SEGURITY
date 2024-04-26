package JWT.JAVA.PROJECT.security.service;

import java.util.Optional;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import JWT.JAVA.PROJECT.security.dto.CreateTweetDto;
import JWT.JAVA.PROJECT.security.dto.HttpResponse;
import JWT.JAVA.PROJECT.security.dto.HttpResponseList;
import JWT.JAVA.PROJECT.security.model.Tweet;
import JWT.JAVA.PROJECT.security.model.User;
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

    public ResponseEntity<HttpResponse> CreateTweet(CreateTweetDto dto, JwtAuthenticationToken token){
        
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        
        String tweetOptional = user.map(userCon -> {
            var tweet = new Tweet();
            tweet.setUser(userCon);
            tweet.setContent(dto.content());
            tweetRepository.save(tweet);
            return "Publicação criada!";
        }).orElse("Usuário não encontrado!");

        return ResponseEntity.ok(new HttpResponse(HttpStatus.CREATED, tweetOptional));

    }

    public ResponseEntity<HttpResponse> DeleteTweet(String tweetId, JwtAuthenticationToken token){

        var user = userRepository.findById(UUID.fromString(token.getName()));

        Optional<Tweet> tweet = tweetRepository.findById(UUID.fromString(tweetId));

        String tweetOptional = tweet.map(tweetDelete -> {

            var isAdmin = user.get().getRoles().stream().anyMatch(role -> role.getName().equalsIgnoreCase("admin"));
            var isMine = tweetDelete.getUser().getUserId().toString().equals(token.getName());

            if (isMine || isAdmin) {

                tweetRepository.deleteById(UUID.fromString(tweetId));
                return "Publicação deletada!";
            }

            return "Usuário não contém as credênciais necessárias!";
        }).orElse("Esse tweet não existe!");

    
        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, tweetOptional));

    }   

    public ResponseEntity<HttpResponse> UpdateTweets(CreateTweetDto dto, String tweetId, JwtAuthenticationToken token){

        Optional<Tweet> optionalTweet = tweetRepository.findById(UUID.fromString(tweetId));

        String tweetUpdated = optionalTweet.map(tweet -> {

            if (!tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
                return "Usuário sem as credenciais necessárias para esta ação!";
            }

            tweet.setContent(dto.content());
            tweetRepository.save(tweet);
            return "Publicação atualizada com sucesso!";
        }).orElse("Publicação não encontrada!");


        return ResponseEntity.ok(new HttpResponse(HttpStatus.OK, tweetUpdated));
    }

    public ResponseEntity<HttpResponseList> GetTweets(){

        return ResponseEntity.ok(new HttpResponseList(HttpStatus.OK, tweetRepository.findTweetsWithoutUserData()));
    }
   
}