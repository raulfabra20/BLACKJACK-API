package s05.t01.handler;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import s05.t01.service.PlayerService;

import java.util.Map;

@Component
public class PlayerHandler {
    private PlayerService playerService;

    public PlayerHandler(PlayerService playerService) {
        this.playerService = playerService;
    }

    @Operation(summary = "Get a ranking with all the player's score")
    public Mono<ServerResponse> getRanking(ServerRequest request){
        return playerService.createRanking()
                .collectList().flatMap(players -> {
                    if (players.isEmpty()) {
                        return ServerResponse.ok().bodyValue("No players found.");
                    }
                    return ServerResponse.ok().bodyValue(players);
                })
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal Server Error: " + e.getMessage()));
    }


    @Operation (
            summary = "Modify the username of the current player",
            parameters = {
                    @Parameter(name = "playerId", description = "ID of the player to modify", required = true)
            })
    @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "JSON body containing the new username", required = true,
            content = @Content(schema = @Schema(example = "{\"username\": \"newUsername\"}")))
    public Mono<ServerResponse> modifyPlayer(ServerRequest request){
        int playerId = Integer.parseInt(request.pathVariable("playerId"));
        return playerService.findPlayerById(playerId)
                .flatMap(player -> request.bodyToMono(Map.class)
                        .flatMap(body -> {
                            String username = (String) body.get("username");
                            return playerService.changeNamePlayer(playerId, username);
                        }))
                .flatMap(updatedPlayer -> ServerResponse.ok().bodyValue(updatedPlayer))
                .switchIfEmpty(ServerResponse.notFound().build())
                .onErrorResume(e -> ServerResponse.status(500).bodyValue("Internal Server Error " +e.getMessage()));
    }
}
