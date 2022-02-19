import at.hassmann.objects.Card;
import at.hassmann.enums.CardType;
import at.hassmann.objects.Cards;
import at.hassmann.enums.ElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class CardsTest {
    private Cards cards, cards2, cards3;
    private Card newCard;
    private Card card;
    private ArrayList<Card> cardsArrayList;

    @Test
    void test_addCard() {
        System.out.println(cards.getCards().toString());
        cards.addCard(newCard);
        System.out.println(cards.getCards().toString());
        System.out.println(cards2.getCards().toString());
        assertTrue(cards.equals(cards2));
    }

    @BeforeEach
    void setUp() {
        card = new Card("Name", 10, ElementType.WATER, CardType.MONSTER);
        newCard = new Card("NameNew", 10, ElementType.WATER, CardType.SPELL);
        Card newerCard = new Card("NameNewerer", 100, ElementType.FIRE, CardType.MONSTER);
        cardsArrayList = new ArrayList<>();
        ArrayList<Card> cardsArrayList2 = new ArrayList<>();
        ArrayList<Card> cardsArrayList3 = new ArrayList<>();
        cardsArrayList.add(card);
        cardsArrayList2.add(newCard);
        cardsArrayList2.add(card);
        cardsArrayList3.add(newerCard);
        cards = new Cards(cardsArrayList);
        cards2 = new Cards(cardsArrayList2);
        cards3 = new Cards(cardsArrayList3);
    }

    @Test
    void test_addCard2() {
        cards.addCard(newCard);
        assertFalse(cards.equals(cards3));
    }

    @Test
    void test_equals() {
        assertFalse(cards.equals(cards2));
    }

    @Test
    void test_contains() {
        assertTrue(cards2.containsCard(card.getName()));
    }

    @Test
    void test_contains2() {
        assertFalse(cards.containsCard(newCard.getName()));
    }

    @Test
    void test_del_Card() {
        cards.addCard(newCard);
        cards.delCard(newCard);
        assertEquals(cardsArrayList, cards.getCards());
    }

    @Test
    void test_getCards() {
        assertEquals(cardsArrayList, cards.getCards());
    }
}
