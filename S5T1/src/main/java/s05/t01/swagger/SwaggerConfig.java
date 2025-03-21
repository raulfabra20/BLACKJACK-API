package s05.t01.swagger;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springdoc.core.models.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(info = @Info(title = "Blackjack API", version = "v1"))
public class SwaggerConfig{

    @Bean
    public GroupedOpenApi blackjackApi(){
        return GroupedOpenApi.builder()
                .group("api")
                .pathsToMatch("/game/**", "/player/**", "/ranking")
                .build();
    }
}
