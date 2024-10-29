package s05.t01.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class Crupier {
    private List<Card> hand;
    private static Logger log = LoggerFactory.getLogger(Game.class);

    public Crupier() {
        this.hand = new ArrayList<>();
    }

    public List<Card> getHand() {
        return hand;
    }

    public void setHand(List<Card> hand) {
        this.hand = hand;
    }

    public void addCard(Card card){
        hand.add(card);
    }

    public void showFirstCard(){
        log.info("Card: "+hand.get(0).getRank()+" ("+hand.get(0).getValueCard()+") of "+hand.get(0).getSuit());
    }

    public void showHand(){
        int totalValue = getValueHandCrupier();
        hand.forEach(c -> log.info("Card: "+c.getRank()+" of "+c.getSuit()));
        log.info("Total cards value: "+totalValue);

    }
    public int getValueHandCrupier() {
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
}

