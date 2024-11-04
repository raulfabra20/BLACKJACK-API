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
                    Game game = new Game(existingPlayer);
                    log.info("Initializing game for Player ID: {}, Username: {}", existingPlayer.getPlayerId(), existingPlayer.getUsername());
                    return gameRepository.save(game).doOnNext(savedGame -> {
                        log.info("New game created with ID : {}", savedGame.getId());
                    });
                });
    }

    public Mono<Game> getGameById(String gameId) {
        return gameRepository.findById(gameId);
    }

    public Mono<Game> playGame(String move, String gameId) {
        return gameRepository.findById(gameId).flatMap(game -> {
            if(game.isFinished()){
                return Mono.error(new IllegalStateException("The game has already ended."));
            }
            return processMove(move, game)
                    .doOnNext(updatedGame -> log.info("Updated Game ID: {}, Player's Hand: {}, Crupier's Hand: {}",
                            updatedGame.getId(), updatedGame.getPlayerHand(), updatedGame.getCrupier().getHand()))
                    .flatMap(gameRepository::save);
        });
    }

    public Mono<Game> startGame(String gameId) {
        return gameRepository.findById(gameId)
                .flatMap(game -> {
                    if (game.getPlayerHand().isEmpty() && game.getCrupier().getHand().isEmpty()) {
                        game.handDeckPlayers();

                    } else{
                        game.resetHands();
                        game.handDeckPlayers();
                        game.setStatus("in progress");
                    }
                    log.info("Hand player: "+game.getPlayerHand()+"\n Crupier hand: "+game.getCrupier().getHand());
                   return checkImmediateWinner(game)
                            .flatMap(result -> {
                                if (!"continue".equals(result)) {
                                    return endGame(game, result).flatMap(gameRepository::save);
                                }
                                return gameRepository.save(game);
                            });
                })
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Game with ID: "+gameId+ " not found.")));
    }

    public Mono<String> checkImmediateWinner(Game game) {
        int valuePlayer = game.getValueHand();
        int valueCrupier = game.getCrupier().getValueHandCrupier();

        if (valuePlayer == 21) {
            game.setStatus("Player:" + valuePlayer + " Crupier:" + valueCrupier + "  Yass! You win!");
            return Mono.just("winner");
        } else if (valueCrupier == 21) {
            game.setStatus("Player:" + valuePlayer + " Crupier:" + valueCrupier + "  Oops! You lose!");
            return Mono.just("loser");
        }
        return Mono.just("continue");
    }

    public Mono<Game> processMove(String move, Game game) {
        switch (move.toLowerCase()) {
            case "start":
                return startGame(game.getId());
            case "hit":
                if (game.isFinished()) {
                    return Mono.error(new IllegalStateException("Game is already finished."));
                }
                return Mono.just(game)
                        .flatMap(g -> {
                            g.addCard(g.dealCard());
                            log.info("Card dealt to player, current hand: {}", g.getPlayerHand());
                            if (g.getValueHand() > 21) {
                                return endGame(g, "loser").flatMap(gameRepository::save);
                            }
                            return gameRepository.save(g);
                        });
            case "stand":
                if (game.isFinished()) {
                    return Mono.error(new IllegalStateException("Game is already finished."));
                }
               return determineWinner(game).flatMap(result -> endGame(game, result))
                       .flatMap(gameRepository::save);
            case "finish":
                return endGame(game, "finish").flatMap(gameRepository::save);
            default:
                return Mono.error(new IllegalArgumentException("Not a valid move."));
        }
    }

    public Mono<Game> endGame(Game game, String result) {
        int valuePlayer = game.getValueHand();
        int valueCrupier = game.getCrupier().getValueHandCrupier();
        switch (result) {
            case "winner":
                game.setStatus("Player:" + valuePlayer + " Crupier:" + valueCrupier + "  Yass! You win!");
                return playerService.incrementScore(game.getPlayer().getPlayerId(), 5)
                        .then(Mono.just(game));
            case "loser":
                game.setStatus("Player:" + valuePlayer + " Crupier:" + valueCrupier + "  Oops! You lose!");
                return Mono.just(game);
            case "tie":
                game.setStatus("Player:" + valuePlayer + " Crupier:" + valueCrupier + "   OhWow! It's a tie!");
                return Mono.just(game);
            case "finish":
                game.setStatus("Game finished.");
                game.setFinished(true);
                return Mono.just(game);
            default:
                return Mono.error(new IllegalArgumentException("Invalid game result"));
        }
    }

    public Mono<String> determineWinner(Game game) {
        int valuePlayer = game.getValueHand();
        int valueCrupier = game.getCrupier().getValueHandCrupier();
        while (valueCrupier < 17) {
            game.dealCardCroupier(1);
            valueCrupier = game.getCrupier().getValueHandCrupier();
        }
        if (valuePlayer > 21) {
            return Mono.just("loser");
        } else if (valueCrupier > 21 || valuePlayer > valueCrupier) {
            return Mono.just("winner");
        } else if (valuePlayer == valueCrupier) {
            return Mono.just("tie");
        } else {
            return Mono.just("loser");
        }
    }

    public Mono<Game> removeGame(String gameId){
        return gameRepository.findById(gameId)
                .flatMap(existingGame -> gameRepository.delete(existingGame).then(Mono.just(existingGame)))
                .switchIfEmpty(Mono.error(new IllegalArgumentException("Game with id: "+gameId+" not found.")));
    }
}