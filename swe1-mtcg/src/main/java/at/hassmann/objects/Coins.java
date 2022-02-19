package at.hassmann.objects;

/**
 * all coins of a user
 */
public class Coins {
    private final int amount;

    /**
     * creats coin object
     * @param coins all coins of the user
     */
    public Coins(int coins) {
        this.amount = coins;
    }

    /**
     * get all coins
     * @return all coins
     */
    public int getCoinAmount() {
        return this.amount;
    }
}
