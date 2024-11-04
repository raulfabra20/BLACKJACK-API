package s05.t01.handlers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import s05.t01.handler.GameHandler;
import s05.t01.model.Game;
import s05.t01.model.Player;
import s05.t01.service.GameService;
import s05.t01.service.PlayerService;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

public class GameHandlerTest {

    @InjectMocks
    private GameHandler gameHandler;

    @Mock
    private PlayerService playerService;

    @Mock
    private GameService gameService;

    @Mock
    private ServerRequest request;

    @BeforeEach
    void setUp(){
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void createGameTest() {
        Player player = new Player("testPlayer");

        when(request.bodyToMono(Player.class)).thenReturn(Mono.just(player));
        when(playerService.findPlayerById(any())).thenReturn(Mono.empty());
        when(playerService.createPlayer(any())).thenReturn(Mono.just(player));
        when(gameService.initializeGame(any())).thenReturn(Mono.just(new Game(player)));

        Mono<ServerResponse> response = gameHandler.createGame(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    return serverResponse.statusCode().value() == 201;
                })
                .verifyComplete();
    }

    @Test
    void getGameDetailsTest() {
        Game game = new Game(new Player("testPlayer"));
        when(request.pathVariable("id")).thenReturn("gameId");
        when(gameService.getGameById("gameId")).thenReturn(Mono.just(game));

        Mono<ServerResponse> response = gameHandler.getGameDetails(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    return serverResponse.statusCode().value() == 200;
                })
                .verifyComplete();
    }

    @Test
    void makeMoveTest() {
        Game game = new Game(new Player("testPlayer"));
        when(request.pathVariable("id")).thenReturn("gameId");
        when(gameService.getGameById("gameId")).thenReturn(Mono.just(game));
        when(request.bodyToMono(Map.class)).thenReturn(Mono.just(Map.of("move", "hit")));
        when(gameService.playGame("hit", "gameId")).thenReturn(Mono.just(game));

        Mono<ServerResponse> response = gameHandler.makeMove(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    return serverResponse.statusCode().value() == 200;
                })
                .verifyComplete();
    }

    @Test
    void deleteGameTest() {
        when(request.pathVariable("id")).thenReturn("gameId");
        when(gameService.removeGame("gameId")).thenReturn(Mono.just(new Game(new Player("testPlayer"))));

        Mono<ServerResponse> response = gameHandler.deleteGame(request);

        StepVerifier.create(response)
                .expectNextMatches(serverResponse -> {
                    return serverResponse.statusCode().value() == 200;
                })
                .verifyComplete();
    }
}
