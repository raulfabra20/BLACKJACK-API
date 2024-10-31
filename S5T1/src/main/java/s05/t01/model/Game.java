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
        this.status = "In progress";
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
        dealHandPlayer(2);
        dealCardCroupier(2);
    }

    public Card dealCard() {
        Card dealtCard = deck.dealCard();
        return dealtCard;
    }

    public void dealHandPlayer(int numberOfCards){
        for(int i = 0; i < numberOfCards; i++){
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

    public void resetHands() {
        this.player.resetHand();
        this.crupier.resetHand();
    }
}









