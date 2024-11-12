package s05.t01.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import s05.t01.model.Game;
import s05.t01.model.Player;
import s05.t01.service.GameService;
import s05.t01.service.PlayerService;

import java.util.Map;

@Component
public class GameHandler {
    private PlayerService playerService;
    private GameService gameService;
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public GameHandler(GameService gameService,PlayerService playerService) {
        this.gameService = gameService;
        this.playerService = playerService;
    }

    @Operation(
            summary = "Create a new game for a player",
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Player object containing the username",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = Player.class, example = "{\"username\": \"player1\"}"))))
    public Mono<ServerResponse> createGame(ServerRequest request) {
        return request.bodyToMono(Player.class)
                .flatMap(player -> {
                    if (player.getUsername() == null || player.getUsername().isEmpty()) {
                        return ServerResponse.status(400).bodyValue("Player's username is required.");
                    }
                    return playerService.findPlayerById(player.getPlayerId())
                            .flatMap(existingPlayer -> {
                                return ServerResponse.status(409).bodyValue("This player already exists. Create another one.");
                            })
                            .switchIfEmpty(
                                    playerService.createPlayer(new Player(player.getUsername()))
                                            .flatMap(newPlayer -> gameService.initializeGame(newPlayer))
                                            .flatMap(game -> {
                                                String createdResponse = "Game with id :" + game.getId() + " successfully created.";
                                                return ServerResponse.status(201).bodyValue(createdResponse);
                                            })
                            );
                })
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Error: " + e.getMessage()));
    }


    @Operation(
            summary = "Get details of the current game"
           ,parameters = {
                    @Parameter(name = "id", description = "ID of the game", required = true),
            })
    public Mono<ServerResponse> getGameDetails(ServerRequest request) {
        String gameId = request.pathVariable("id");
        return gameService.getGameById(gameId).flatMap(game -> ServerResponse.ok().bodyValue(game))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error fetching game details for ID: " + gameId, e);
                    return ServerResponse.status(500).bodyValue("Internal Server Error: " + e.getMessage());
                });
    }


    @Operation(
            summary = "The player chooses which move to make",
            description = "Allows the player to choose a move in the game with the specified ID",
            parameters = {
                    @Parameter(name = "id", description = "ID of the game", required = true)
            })
             @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Move to be made in the game, provided in the body as JSON", required = true,
                    content = @Content(schema = @Schema(example = "{\"move\": \"hit\"}")))
    public Mono<ServerResponse> makeMove(ServerRequest request) {
        String gameId = request.pathVariable("id");
        return gameService.getGameById(gameId)
                .flatMap(game -> request.bodyToMono(Map.class)
                        .flatMap(body -> {
                            String move = (String) body.get("move");
                            return gameService.playGame(move, gameId);
                        })
                        .flatMap(updatedGame -> ServerResponse.ok().bodyValue(updatedGame))
                )
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal Server Error " + e.getMessage()));
    }


    @Operation(
            summary = "Delete a game",
            parameters = {
                    @Parameter(name = "id", description = "ID of the game", required = true)
    })
    public Mono<ServerResponse> deleteGame(ServerRequest request) {
        String gameId = request.pathVariable("id");
        return gameService.removeGame(gameId)
                .flatMap(removedGame  ->
                        ServerResponse.ok().bodyValue("Game with id "+gameId+" deleted successfully."))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Game with id "+gameId+" not found."));
    }

    @Operation(summary = "Root endpoint")
    public Mono<ServerResponse> handleRootRequest(ServerRequest request) {
        return ServerResponse.ok().bodyValue("Welcome to my Blackjack Api!");
    }
}






