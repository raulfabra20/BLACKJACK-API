package s05.t01.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import reactor.core.publisher.Mono;
import s05.t01.exception.NoCardsAvailableException;


@Document (collection = "game")
public class Game {
    @Id
    private String id;
    private Player player;
    private Crupier crupier;
    private DeckCards deck;
    private boolean isFinished;
    private String status;
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public Game(){

    }

    public Game(String username) {
        this.player = new Player(username);
        this.crupier = new Crupier();
        this.deck = new DeckCards();
        this.isFinished = false;
        this.status = "IN_PROGRESS";
    }

    public String getId() {
        return id;
    }

    public Player getPlayer() {
        return player;
    }

    public Crupier getCrupier() {
        return crupier;
    }

    public DeckCards getDeck() {
        return deck;
    }

    public String getStatus() {
        return status;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setCrupier(Crupier crupier) {
        this.crupier = crupier;
    }

    public void setDeck(DeckCards deck) {
        this.deck = deck;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isFinished() {
        return isFinished;
    }

    public void setFinished(boolean finished) {
        isFinished = finished;
    }

    public void handDeckPlayers() throws NoCardsAvailableException {
        deck.shuffleDeck();
        dealCard(2);
        dealCardCroupier(2);
    }

    public void dealCard(int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            Card dealtCard = deck.dealCard();
            player.addCard(dealtCard);
        }
    }

    public void dealCardCroupier(int numberOfCards) {
        for (int i = 0; i < numberOfCards; i++) {
            Card dealtCard = deck.dealCard();
            crupier.addCard(dealtCard);
        }
    }

    public void startGame(){
        handDeckPlayers();
        player.showHand();
        crupier.showFirstCard();
        String result = checkImmediateWinner(this);
        if (!result.equals("continue")) {
            endGame(this, result).subscribe();
        }
    }

    public Mono<Game> processMove(String move, Game game) {
       /* if ("hit".equalsIgnoreCase(move)) {
            game.dealCard(1);
            game.getPlayer().showHand();
            if (game.getPlayer().getValueHand() > 21) {
                return endGame(game, "loser");
            }
            return Mono.just(game);
        } else if ("stand".equalsIgnoreCase(move)) {
            String result = determineWinner(game);
            return endGame(game, result);
        } else {
            return Mono.error(new IllegalArgumentException("Not a valid move. You must 'hit' or 'stand'."));
        }*/
        switch (move) {
            case "start":
                game.startGame();
                break;
            case "hit":
                game.dealCard(1);
                game.getPlayer().showHand();
                if (game.getPlayer().getValueHand() > 21) {
                    return endGame(game, "loser");
                }
                return Mono.just(game);
                break;
            case "stand":
                String result = determineWinner(game);
                return endGame(game, result);
                break;
            case "finish":

        }

    }

    public Mono<Game> endGame(Game game, String result) {
        switch (result) {
            case "winner":
                game.getPlayer().setScore(game.getPlayer().getScore()+1);
                game.setStatus("WIN");
                break;
            case "loser":
                game.setStatus("LOSE");
                break;
        }
        return Mono.just(game);
    }

    public String checkImmediateWinner(Game game) {
        int valuePlayer = game.getPlayer().getValueHand();
        int valueCrupier = game.getCrupier().getValueHandCrupier();

        if (valuePlayer == 21) {
            logGameResult(valuePlayer, valueCrupier, "Yass! You win!");
            return "winner";
        } else if (valueCrupier == 21) {
            logGameResult(valuePlayer, valueCrupier, "Oops! You lose!");
            return "loser";
        }
        return "continue";
    }

    public String determineWinner(Game game) {
        int valuePlayer = game.getPlayer().getValueHand();
        int valueCrupier = game.getCrupier().getValueHandCrupier();
        while (valueCrupier < 17) {
            game.dealCardCroupier(1);
            valueCrupier = game.getCrupier().getValueHandCrupier();
        }
        if (valuePlayer > 21) {
            return logAndReturnResult(valuePlayer, valueCrupier, "Oops! You lose!", "loser");
        }
        if (valueCrupier > 21) {
            return logAndReturnResult(valuePlayer, valueCrupier, "Yass! You win!", "winner");
        }
        if (valuePlayer > valueCrupier) {
            return logAndReturnResult(valuePlayer, valueCrupier, "Yass! You win!", "winner");
        } else if (valuePlayer < valueCrupier) {
            return logAndReturnResult(valuePlayer, valueCrupier, "Oops! You lose!", "loser");
        } else {
            return logAndReturnResult(valuePlayer, valueCrupier, "OhWow! It's a tie!", "tie");
        }
    }

    private void logGameResult(int valuePlayer, int valueCrupier, String message) {
        log.info("Player: " + valuePlayer + "\nCrupier: " + valueCrupier + "\n" + message);
    }

    private String logAndReturnResult(int valuePlayer, int valueCrupier, String message, String result) {
        logGameResult(valuePlayer, valueCrupier, message);
        return result;
    }
}









