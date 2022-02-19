import at.hassmann.objects.Card;
import at.hassmann.objects.Cards;
import at.hassmann.objects.Package;
import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class PackageTest {
    private Package myPackage;
    @BeforeEach
    void setUp() {
        Card card = new Card("Name", 10, ElementType.WATER, CardType.MONSTER);
        ArrayList<Card> cardsArrayList = new ArrayList<>();
        cardsArrayList.add(card);
        Cards cards = new Cards(cardsArrayList);
        myPackage = new Package(cards,"Name", 100);
    }

    @Test
    void test_getName() {
        String result = myPackage.getName();
        assertEquals(result, "Name");
    }

    @Test
    void test_getPrice() {
        int result = myPackage.getPrice();
        assertEquals(result, 100);
    }

    @Test
    void test_setName() {
        myPackage.setName("neuName");
        String result = myPackage.getName();
        assertEquals(result, "neuName");
    }
}
