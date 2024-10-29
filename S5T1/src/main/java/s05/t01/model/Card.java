package s05.t01.model;

public class Card {

    private Rank rank;
    private Suit suit;

    public Card(Rank rank, Suit suit) {
        this.rank = rank;
        this.suit = suit;
    }

    public Rank getRank() {
        return rank;
    }

    public Suit getSuit() {
        return suit;
    }

    public int getValueCard(){
        return rank.getValue();
    }

    @Override
    public String toString() {
        return "Card{ rank=" + rank +
                ", suit=" + suit +
                '}';
    }
}
