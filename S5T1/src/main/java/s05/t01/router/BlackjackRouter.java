package s05.t01.router;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
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
    @RouterOperations({
            @RouterOperation(
                    path = "/game/new",
                    beanClass = GameHandler.class,
                    beanMethod = "createGame",
                    operation = @Operation(
                            summary = "Create a new game",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Game created successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid game creation request")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/game/{id}",
                    beanClass = GameHandler.class,
                    beanMethod = "getGameDetails",
                    operation = @Operation(
                            summary = "Get details of the current game",
                            operationId = "getGameDetails",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the game", required = true),
                            responses  = {
                                    @ApiResponse(responseCode = "200", description = "Game details retrieved successfully"),
                                    @ApiResponse(responseCode = "404", description = "Game not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/game/{id}/play",
                    beanClass = GameHandler.class,
                    beanMethod = "makeMove",
                    operation = @Operation(
                            summary = "Make a move in the current game",
                            operationId = "makeAMoveGame",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the game", required = true),
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Move made successfully"),
                                    @ApiResponse(responseCode = "400", description = "Invalid move")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/game/{id}/delete",
                    beanClass = GameHandler.class,
                    beanMethod = "deleteGame",
                    operation = @Operation(
                            summary = "Delete the current game",
                            operationId = "removeTheGame",
                            parameters = @Parameter(name = "id", in = ParameterIn.PATH, description = "ID of the game", required = true),
                            responses =  {
                                    @ApiResponse(responseCode = "200", description = "Game deleted successfully"),
                                    @ApiResponse(responseCode = "404", description = "Game not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/ranking",
                    beanClass = PlayerHandler.class,
                    beanMethod = "getRanking",
                    operation = @Operation(
                            summary = "Get player ranking",
                            responses = {
                                    @ApiResponse(responseCode = "200", description = "Ranking retrieved successfully"),
                                    @ApiResponse(responseCode = "404", description = "Ranking not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/player/{playerId}",
                    beanClass = PlayerHandler.class,
                    beanMethod = "modifyPlayer",
                    operation = @Operation(
                            summary = "Modify player's username",
                            operationId = "modifyAPlayer",
                            parameters = @Parameter(name = "playerId", in = ParameterIn.PATH, description = "ID of the player", required = true),
                            responses =  {
                                    @ApiResponse(responseCode = "200", description = "Player modified successfully"),
                                    @ApiResponse(responseCode = "404", description = "Player not found")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/",
                    beanClass = GameHandler.class,
                    beanMethod = "handleRootRequest",
                    operation = @Operation(
                            summary = "Root endpoint",
                            responses =  {
                                    @ApiResponse(responseCode = "200", description = "Welcome to the Blackjack API")
                            }
                    )
            )
    })
    public RouterFunction<ServerResponse> routeBlackjack(GameHandler gameHandler, PlayerHandler playerHandler ){
        return route()
                .POST("/game/new", gameHandler::createGame)
                .GET("/game/{id}", gameHandler::getGameDetails)
                .POST("/game/{id}/play", gameHandler::makeMove)
                .DELETE("/game/{id}/delete", gameHandler::deleteGame)
                .GET("/ranking", playerHandler::getRanking)
                .PUT("/player/{playerId}", playerHandler::modifyPlayer)
                .GET("/", gameHandler::handleRootRequest)
                .build();
    }
}
