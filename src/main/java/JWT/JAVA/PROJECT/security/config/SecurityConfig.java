package JWT.JAVA.PROJECT.security.config;

import java.io.IOException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.OncePerRequestFilter;

import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@EnableWebSecurity
@EnableMethodSecurity
@Configuration
public class SecurityConfig {
    
    @Value("${jwt.public.key}")
    private RSAPublicKey publicKey;

    @Value("${jwt.private.key}")
    private RSAPrivateKey privateKey;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception{

        httpSecurity.authorizeHttpRequests(authorize -> authorize
        .requestMatchers(HttpMethod.POST, "/login").permitAll()
        .requestMatchers(HttpMethod.POST, "/register").permitAll()
        .requestMatchers(HttpMethod.GET, "/v3/api-docs").permitAll()
        .requestMatchers(HttpMethod.GET, "/swagger-ui/***").permitAll()
        .requestMatchers(HttpMethod.GET, "/v3/api-docs/swagger-config").permitAll()
        .anyRequest().authenticated())
        .csrf(csrf -> csrf.disable()).oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .addFilterBefore(redirectToSwaggerFilter(), UsernamePasswordAuthenticationFilter.class);
        return httpSecurity.build();        
    }

    @Bean
    public JwtEncoder jwtEncoder(){
        JWK jwk = new RSAKey.Builder(this.publicKey).privateKey(privateKey).build();
        var jwks = new ImmutableJWKSet<>(new JWKSet(jwk));
        return new NimbusJwtEncoder(jwks);
    }

    @Bean
    public JwtDecoder jwtDecoder(){
      return NimbusJwtDecoder.withPublicKey(publicKey).build();
    }
    
    @Bean
	public BCryptPasswordEncoder bCryptPasswordEncoder(){
		return new BCryptPasswordEncoder();
	}
    
    @Bean
    public OncePerRequestFilter redirectToSwaggerFilter() {
        return new OncePerRequestFilter() {
            @SuppressWarnings("null")
            @Override
            protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
                if (request.getRequestURI().equals("/") && request.getMethod().equals("GET")) {
                    response.sendRedirect("/swagger-ui/index.html");
                } else {
                    filterChain.doFilter(request, response);
                }
            }
        };
    }
    
}
