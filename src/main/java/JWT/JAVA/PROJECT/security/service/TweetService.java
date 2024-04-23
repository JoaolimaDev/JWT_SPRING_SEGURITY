package JWT.JAVA.PROJECT.security.service;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Service;

import JWT.JAVA.PROJECT.security.config.ViewConfig;
import JWT.JAVA.PROJECT.security.dto.CreateTweetDto;
import JWT.JAVA.PROJECT.security.model.Tweet;
import JWT.JAVA.PROJECT.security.model.User;
import JWT.JAVA.PROJECT.security.repository.TweetRepository;
import JWT.JAVA.PROJECT.security.repository.UserRepository;

@Service
public class TweetService {

    @Autowired
    private ViewConfig viewConfig;

    private final TweetRepository tweetRepository;
    private final UserRepository userRepository;
    
    public TweetService(TweetRepository tweetRepository, UserRepository userRepository) {
        this.tweetRepository = tweetRepository;
        this.userRepository = userRepository;
    }

    public ResponseEntity<Map<String, Object>> CreateTweet(CreateTweetDto dto, JwtAuthenticationToken token){
        
        Optional<User> user = userRepository.findById(UUID.fromString(token.getName()));
        
        String tweetOptional = user.map(userCon -> {
            var tweet = new Tweet();
            tweet.setUser(userCon);
            tweet.setContent(dto.content());
            tweetRepository.save(tweet);
            return "Publicação criada!";
        }).orElse("Usuário não encontrado!");

        return viewConfig.ResponseEntity(HttpStatus.CREATED, tweetOptional);
    }

    public ResponseEntity<Map<String, Object>> DeleteTweet(String tweetId, JwtAuthenticationToken token){

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

    
        return  viewConfig.ResponseEntity(HttpStatus.FORBIDDEN, tweetOptional);

    }   

    public ResponseEntity<Map<String, Object>> UpdateTweets(CreateTweetDto dto, String tweetId, JwtAuthenticationToken token){

        Optional<Tweet> optionalTweet = tweetRepository.findById(UUID.fromString(tweetId));

        String tweetUpdated = optionalTweet.map(tweet -> {

            if (!tweet.getUser().getUserId().equals(UUID.fromString(token.getName()))) {
                return "Usuário sem as credenciais necessárias para esta ação!";
            }

            tweet.setContent(dto.content());
            tweetRepository.save(tweet);
            return "Publicação atualizada com sucesso!";
        }).orElse("Publicação não encontrada!");

        return  viewConfig.ResponseEntity(HttpStatus.OK, tweetUpdated);
    }

    public ResponseEntity<Map<String, Object>> GetTweets(){

        return viewConfig.ResponseEntityList(HttpStatus.OK, tweetRepository.findTweetsWithoutUserData());
    }
   
}