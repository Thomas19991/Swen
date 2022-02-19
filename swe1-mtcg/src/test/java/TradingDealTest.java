import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;
import at.hassmann.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class TradingDealTest {

    private TradingDeal tradingDeal;
    private Card newCard, card;

    @BeforeEach
    void set_Up() {
        card = new Card("Name", 10, ElementType.WATER, CardType.MONSTER);
        newCard = new Card("NameNew", 10, ElementType.WATER, CardType.SPELL);
        Card cardToTrade = card;
        ElementType requiredElementType = ElementType.FIRE;
        double requiredMinDamage = 100;
        CardType requiredCardType = CardType.MONSTER;
        tradingDeal = new TradingDeal("ID", cardToTrade, requiredElementType, requiredMinDamage, requiredCardType, "NAME");
    }

    @Test
    void test_get_CardToTrade() {
        Card result = tradingDeal.getCardToTrade();
        assertTrue(result.equals(card));
    }
    @Test
    void test_get_Required_ElementType() {
        ElementType result = tradingDeal.getRequiredElementType();
        assertSame(result, ElementType.FIRE);
    }
    @Test
    void test_get_Required_Min_Damage() {
        double result = tradingDeal.getRequiredMinDamage();
        assertEquals(result, 100);
    }
    @Test
    void test_get_Required_CardType() {
        CardType result = tradingDeal.getRequiredCardType();
        assertSame(result, CardType.MONSTER);
    }

    @Test
    void test_set_Card_To_Trade() {
        tradingDeal.setCardToTrade(newCard);
        Card result = tradingDeal.getCardToTrade();
        assertSame(result, newCard);
    }
    @Test
    void test_setRequired_Element_Type() {
        tradingDeal.setRequiredElementType(ElementType.WATER);
        ElementType result = tradingDeal.getRequiredElementType();
        assertSame(result, ElementType.WATER);
    }
    @Test
    void test_set_Required_Min_Damage() {
        tradingDeal.setRequiredMinDamage(10);
        double result = tradingDeal.getRequiredMinDamage();
        assertEquals(result, 10);
    }
    @Test
    void test_set_Required_CardType() {
        tradingDeal.setRequiredCardType(CardType.SPELL);
        CardType result = tradingDeal.getRequiredCardType();
        assertSame(result, CardType.SPELL);
    }
}
