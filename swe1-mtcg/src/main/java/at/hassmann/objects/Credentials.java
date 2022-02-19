package at.hassmann.objects;

/**
 * User Login Data
 */
public class Credentials {
    private final String passwort;
    private final String username;

    /**
     * starts new user with this data
     * @param username this username
     * @param passwort this password
     */
    public Credentials(String username, String passwort){
        this.username = username;
        this.passwort = passwort;
    }

    /**
     * get password
     * @return PW of user
     */
    public String getPasswort() {
        return passwort;
    }

    /**
     * get username
     * @return Username of user
     */
    public String getUsername() {
        return username;
    }

}
