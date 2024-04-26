package JWT.JAVA.PROJECT.security.config;

import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;

@Configuration
@SecurityScheme(
  name = "Bearer Authentication",
  type = SecuritySchemeType.HTTP,
  bearerFormat = "JWT",
  scheme = "bearer"
)
@OpenAPIDefinition(
  info =@Info(
    title = "JWT_SPRING_SEGURITY",
    version = "2.2",
    contact = @Contact(
      name = "João vitor de lima", email = "joaolimaprofissional@hotmail.com", url = "https://github.com/JoaolimaDev"
    ),
    description = "Projeto Spring Boot, orientado aos conceitos de autênticação e atorização utilizando a tecnologia JWT."
  ),
  servers = @Server(
    url = "http://localhost:8080",
    description = "Produção"
  )
)
public class OpenAPI30Configuration {}