package s05.t01.model;

import s05.t01.exception.NoCardsAvailableException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DeckCards {
    private List<Card> deckCards;

    public DeckCards() {
        deckCards = new ArrayList<>();
        createDeck();
    }

    private void createDeck(){
        for(Suit suit : Suit.values()){
            for(Rank rank : Rank.values()){
                deckCards.add(new Card(rank, suit));
            }
        }
    }

    public void shuffleDeck(){
        Collections.shuffle(deckCards);
    }

    public Card dealCard() throws NoCardsAvailableException {
        if(!deckCards.isEmpty()){
            return deckCards.remove(0);
        } else {
            throw new NoCardsAvailableException("There are not cards in the deck.");
        }
    }

    public int getDeckSize(){
        return deckCards.size();
    }

















}
