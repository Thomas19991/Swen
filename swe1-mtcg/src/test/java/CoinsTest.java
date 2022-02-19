import at.hassmann.objects.Coins;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CoinsTest {

    @Test
    public void test_getCoinAmount(){
        Coins coin = new Coins(10);
        assertTrue(coin.getCoinAmount() >= 0);
    }

}
