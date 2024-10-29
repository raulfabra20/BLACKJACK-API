package s05.t01.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;
import s05.t01.handler.GameHandler;
import s05.t01.handler.PlayerHandler;

import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Configuration
public class BlackjackRouter {

    @Bean
    public RouterFunction<ServerResponse> routeBlackjack(GameHandler gameHandler, PlayerHandler playerHandler ){
        return route()
                .POST("/game/new", gameHandler::createGame)
                .GET("/game/{id}", gameHandler::getGameDetails)
                .POST("/game/{id}/play", gameHandler::makeMove)
                .DELETE("/game/{id}/delete", gameHandler::deleteGame)
                .GET("/ranking", playerHandler::getRanking)
                .PUT("/player/{playerId}", playerHandler::modifyPlayer)
                .build();

    }

}
