package s05.t01.handler;

import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import s05.t01.service.PlayerService;

import java.util.Map;

public class PlayerHandler {
    private PlayerService playerService;

    public PlayerHandler(PlayerService playerService) {
        this.playerService = playerService;
    }

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
