package s05.t01.handler;

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

    public GameHandler(GameService gameService) {
        this.gameService = gameService;
    }

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

    public Mono<ServerResponse> getGameDetails(ServerRequest request) {
        String gameId = request.pathVariable("id");
        return gameService.getGameById(gameId).flatMap(game -> ServerResponse.ok().bodyValue(game))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> {
                    log.error("Error fetching game details for ID: " + gameId, e);
                    return ServerResponse.status(500).bodyValue("Internal Server Error: " + e.getMessage());
                });

    }

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

    public Mono<ServerResponse> deleteGame(ServerRequest request) {
        String gameId = request.pathVariable("id");
        return gameService.removeGame(gameId)
                .flatMap(removedGame  ->
                        ServerResponse.ok().bodyValue("Game with id "+gameId+" deleted successfully."))
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Game with id "+gameId+" not found."));
    }
}






