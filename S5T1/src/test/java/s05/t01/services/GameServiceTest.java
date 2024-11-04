package s05.t01.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import reactor.core.publisher.Mono;
import s05.t01.model.Game;
import s05.t01.model.Player;
import s05.t01.repository.GameRepository;
import s05.t01.service.GameService;
import s05.t01.service.PlayerService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class GameServiceTest {

    @InjectMocks
    private GameService gameService;

    @Mock
    private PlayerService playerService;

    @Mock
    private GameRepository gameRepository;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void initializeGameTest() {
        Player player = new Player("testPlayer");
        Game game = new Game(player);

        when(playerService.initializePlayer(any())).thenReturn(Mono.just(player));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        gameService.initializeGame(player).block();

        verify(gameRepository, times(1)).save(any(Game.class));
    }

    @Test
    void getGameByIdTest() {
        Game game = new Game(new Player("testPlayer"));
        when(gameRepository.findById(any(String.class))).thenReturn(Mono.just(game));

        gameService.getGameById("gameId").block();

        verify(gameRepository, times(1)).findById("gameId");
    }

    @Test
    void playGameTest() {
        Game game = new Game(new Player("testPlayer"));
        game.setId("gameId");
        game.getPlayer().setPlayerId(1);

        when(gameRepository.findById("gameId")).thenReturn(Mono.just(game));
        when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

        gameService.playGame("hit", "gameId").block();

        verify(gameRepository, times(1)).findById("gameId");
        verify(gameRepository, times(2)).save(any(Game.class));
    }

        @Test
        void startGameTest() {
            Game game = new Game(new Player("testPlayer"));
            when(gameRepository.findById(any(String.class))).thenReturn(Mono.just(game));
            when(gameRepository.save(any(Game.class))).thenReturn(Mono.just(game));

            gameService.startGame("gameId").block();

            verify(gameRepository, times(1)).findById("gameId");
            verify(gameRepository, times(1)).save(any(Game.class));
        }

        @Test
        void removeGameTest () {
            Game game = new Game(new Player("testPlayer"));
            when(gameRepository.findById(any(String.class))).thenReturn(Mono.just(game));
            when(gameRepository.delete(any(Game.class))).thenReturn(Mono.empty());

            gameService.removeGame("gameId").block();

            verify(gameRepository, times(1)).findById("gameId");
            verify(gameRepository, times(1)).delete(any(Game.class));
        }
    }

