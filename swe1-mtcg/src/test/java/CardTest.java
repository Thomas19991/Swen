import at.hassmann.objects.Card;
import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CardTest {

    @Test
    public void test_getName() {
        Card card = new Card("Name", 5, ElementType.WATER, CardType.SPELL);
        assertEquals(card.getName(), "Name");
    }
    @Test
    public void test_getDamage() {
        Card card = new Card("Name", 3, ElementType.WATER, CardType.SPELL);
        assertEquals(card.getDamage(), 3);
    }
    @Test
    public void test_getElementType() {
        Card card = new Card("Name", 8, ElementType.WATER, CardType.SPELL);
        ElementType elementType = card.getElementTyp();
        assertSame(elementType, ElementType.WATER);
    }
    @Test
    public void test_getCardType() {
        Card card = new Card("Name", 7, ElementType.WATER, CardType.SPELL);
        CardType cardType = card.getCardType();
        assertSame(cardType, CardType.SPELL);
    }
    @Test
    public void test_setName() {
        Card card = new Card("Name", 1, ElementType.WATER, CardType.SPELL);
        card.setName("NeuerName");
        assertEquals(card.getName(), "NeuerName");
    }
    @Test
    public void test_setDamage() {
        Card card = new Card("Name", 120, ElementType.WATER, CardType.SPELL);
        card.setDamage(100);
        assertEquals(card.getDamage(), 100);
    }
    @Test
    public void test_setElementTyp() {
        Card card = new Card("Name", 11, ElementType.WATER, CardType.SPELL);
        card.setElementType(ElementType.FIRE);
        assertSame(card.getElementTyp(), ElementType.FIRE);
    }
}
