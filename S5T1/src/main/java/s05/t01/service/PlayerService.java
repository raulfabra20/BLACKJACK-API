package s05.t01.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import s05.t01.exception.NotUpdatedPlayerException;
import s05.t01.model.Game;
import s05.t01.model.Player;
import s05.t01.repository.PlayerRepository;

import java.util.Comparator;
import java.util.NoSuchElementException;

@Service
public class PlayerService {
    private PlayerRepository playerRepository;
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public PlayerService(PlayerRepository playerRepository) {
        this.playerRepository = playerRepository;
    }

    public Mono<Player> initializePlayer(Player player) {
        return playerRepository.findById(player.getPlayerId())
                .switchIfEmpty(Mono.defer(() -> playerRepository.save(player)))
                .doOnNext(savedPlayer -> log.info("Player initialized: {}", savedPlayer.getUsername()));
    }

    public Mono<Player> findPlayerById(Integer playerId) {
        return playerRepository.findById(playerId);
    }

    public Mono<Player> createPlayer(Player player) {
        return playerRepository.save(player);
    }

    public Flux<Player> createRanking() {
        return playerRepository.findAll().sort(Comparator.comparingInt(Player::getScore).reversed());
    }

    public Mono<Player> changeNamePlayer(int playerId, String username) {
        return playerRepository.findById(playerId)
                .flatMap(player -> {
                    player.setUsername(username);
                    return playerRepository.save(player);
                })
                .switchIfEmpty(Mono.error(new NoSuchElementException("Player with id: " + playerId + " not found.")))
                .onErrorResume(e -> {
                    log.error("Error updating player: {}", e.getMessage());
                    return Mono.error(new NotUpdatedPlayerException("Player with id: " + playerId + " not updated."));
                });
    }

    public Mono<Player> incrementScore(int playerId, int increment) {
        log.info("Fetching player with ID: {}", playerId);
        return playerRepository.findById(playerId)
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Player not found with ID: {}", playerId);
                    return Mono.error(new IllegalArgumentException("Player not found with ID: " + playerId));
                }))
                .flatMap(player -> {
                    int updatedScore = player.getScore() + increment;
                    player.setScore(updatedScore);
                    log.info("Updating Player ID: {}, New Score: {}", playerId, updatedScore);
                    return playerRepository.save(player);
                });
    }
}
