package s05.t01.model;



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;


import java.util.ArrayList;
import java.util.List;



@Table("player")
public class Player {

    @Id
    private int playerId;

    @Column("username")
    private String username;

   @Column("score")
    private int score = 0;

   @Transient
   private List<Card> hand;

   private static Logger log = LoggerFactory.getLogger(Game.class);


    public Player(){
        this.playerId = playerId;
        this.username = username;
        this.score = getScore();
        this.hand = new ArrayList();
    }

    public Player(String username) {
        this.playerId = playerId;
        this.username = username;
        this.score = score;
        this.hand = new ArrayList();
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setScore(int score) {
        this.score = score;
    }


    public int getScore(){
        return score;
    }

    public List<Card> getHand() {
        return hand;
    }

    public void addCard(Card card){
        hand.add(card);
    }

    public void showHand(){
        int totalValue = getValueHand();
        hand.forEach(c -> log.info("Card: "+c.getRank()+" of "+c.getSuit()));
        log.info("Total cards value: "+totalValue);

    }

    public int getValueHand() {
        int totalValue = 0;
        int aceCount = 0;

        for (Card card : hand) {
            totalValue += card.getRank().getValue();
            if (card.getRank() == Rank.ACE) {
                aceCount++;
            }
        }
        while (totalValue > 21 && aceCount > 0) {
            totalValue -= 10;
            aceCount--;
        }
        return totalValue;
    }

    public void increaseScore(){
        this.score++;
    }




}
