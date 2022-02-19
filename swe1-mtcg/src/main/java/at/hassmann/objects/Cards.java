package at.hassmann.objects;

import com.fasterxml.jackson.annotation.*;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Administrates the list of cards
 */
@JsonAutoDetect
public class Cards {
    @JsonDeserialize(as = ArrayList.class, contentAs = Card.class)
    private ArrayList<Card> cards;

    /**
     * creates new object with given cards
     * @param cardsArrayList cards to create object
     */
    @JsonCreator
    public Cards(@JsonProperty("cards") ArrayList<Card> cardsArrayList) {
        this.cards = cardsArrayList;
    }

    /**
     * set cards
     * @param cards Cards in ArrayList which should be set
     */
    public void setCards(ArrayList<Card> cards) {
        this.cards = cards;
    }

    /**
     * adds new card
     * @param newCard neue Card
     */
    @JsonSetter
    public void addCard(Card newCard) {
        this.cards.add(newCard);
    }

    /**
     * Get all cards
     * @return all Cards
     */
    @JsonGetter
    public ArrayList<Card> getCards() {
        return this.cards;
    }

    /**
     * deletes given Card
     * @param delCard Card to delete
     */
    public void delCard(Card delCard) {
        this.cards.removeIf(obj -> obj.equals(delCard));
    }

    /**
     * checks if card is contained in cards
     * @param toCeck card which is searched for
     * @return True if card is contained
     */
    public boolean containsCard(String toCeck){
        AtomicBoolean returnval = new AtomicBoolean(false);
        this.cards.forEach(item -> returnval.set(item.getName().equals(toCeck)));
        return returnval.get();
    }

    /**
     * compares 2 card objects
     * @param toCompare Cards to compare
     * @return True if objects contain the same card
     */
    public boolean equals(Cards toCompare){
        if (this.cards == null && toCompare.getCards() == null){
            return true;
        }else if ((this.cards == null && toCompare.getCards() != null) || (this.cards != null && toCompare.getCards() == null)){
            return false;
        }else return Objects.requireNonNull(this.cards).containsAll(toCompare.getCards()) && toCompare.getCards().containsAll(this.cards);
    }
}