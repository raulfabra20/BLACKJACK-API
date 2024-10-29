package s05.t01.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import s05.t01.model.Game;
import s05.t01.model.Player;
import s05.t01.repository.GameRepository;

@Service
public class GameService {

    private PlayerService playerService;
    private GameRepository gameRepository;
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public GameService(PlayerService playerService, GameRepository gameRepository) {
        this.playerService = playerService;
        this.gameRepository = gameRepository;
    }

    public Mono<Game> initializeGame(Player player) {
        return playerService.initializePlayer(player)
                .flatMap(existingPlayer -> {
                    Game game = new Game(player.getUsername());
                    return gameRepository.save(game).doOnNext(savedGame -> {
                        log.info("New game created with ID : {}", savedGame.getId());
                    });
                });
    }

    public Mono<Game> getGameById(String gameId) {
        return gameRepository.findById(gameId);
    }

   /* public Mono<Game> playGame(String move, String gameId) {
        return gameRepository.findById(gameId).flatMap(game -> {
            if (!"IN_PROGRESS".equalsIgnoreCase(game.getStatus())) {
                return Mono.error(new IllegalStateException("The game has already ended."));
            }

            String immediateResult = game.checkImmediateWinner(game);
            if (!"continue".equals(immediateResult)) {
                return game.endGame(game, immediateResult);
            }

            return game.processMove(move, game).flatMap(updatedGame -> {
                if ("stand".equalsIgnoreCase(move)) {
                    String result = game.determineWinner(updatedGame);
                    return game.endGame(updatedGame, result);
                }
                return Mono.just(updatedGame);
            });
        });
    } */

    public Mono<Game> playGame(String move, String gameId) {
        return gameRepository.findById(gameId).flatMap(game -> {
            if(!game.isFinished()){
                return Mono.error(new IllegalStateException("The game has already ended."));
            }

            return game.processMove(move, game).flatMap(updatedGame -> {

            }
        }
    }

    public Mono<Game> removeGame(String gameId){
        return gameRepository.findById(gameId)
                .flatMap(existingGame -> gameRepository.delete(existingGame).then(Mono.just(existingGame)))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Game with id: "+gameId+" not found.")));
    }

}

