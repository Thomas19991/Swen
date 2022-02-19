import at.hassmann.objects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class UserTest {
    private User user;

    @BeforeEach
    void setUp() {
        Coins coins = new Coins(5);
        Credentials credentials = new Credentials("username", "pw1");
        user = new User(credentials, "name", "nachname", coins, "BIO", "IMAGE");
    }
    @Test
    public void test_getName() {
        String result = user.getName();
        assertEquals(result, "name");
    }
    @Test
    public void test_getNachname() {
        String result = user.getNachname();
        assertEquals(result, "nachname");
    }

    @Test
    public void test_setName() {
        String newstring = "new";
        user.setName(newstring);
        String result = user.getName();
        assertEquals(newstring, result);
    }
    @Test
    public void test_setNachname() {
        String newstring = "new";
        user.setNachname(newstring);
        String result = user.getNachname();
        assertEquals(newstring, result);
    }
}
