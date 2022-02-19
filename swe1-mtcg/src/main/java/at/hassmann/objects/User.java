package at.hassmann.objects;
import com.fasterxml.jackson.annotation.*;

/**
 * User Object
 */
@JsonAutoDetect
public class User{
    @JsonProperty
    private String name, nachname, bio, image;
    @JsonProperty
    private final Credentials credentials;

    @JsonCreator
    public User(@JsonProperty Credentials credentials, @JsonProperty String name, @JsonProperty String nachname, @JsonProperty Coins coins, @JsonProperty String bio, @JsonProperty String image) {
        this.credentials = credentials;
        this.name = name;
        this.nachname = nachname;
        this.bio = bio;
        this.image = image;
    }

    /**
     * get Bio of User
     * @return Bio of User
     */
    @JsonGetter
    public String getBio() {
        return bio;
    }

    /**
     * set Bio of User
     * @param bio Bio of User
     */
    @JsonSetter
    public void setBio(String bio) {
        this.bio = bio;
    }

    /**
     * get image of user
     * @return Image of user
     */
    @JsonGetter
    public String getImage() {
        return image;
    }

    /**
     * Set image of user
     * @param image Image of user
     */
    @JsonSetter
    public void setImage(String image) {
        this.image = image;
    }

    /**
     * get exact login data of user
     * @return exact Login data
     */
    @JsonGetter
    public Credentials getCredentials() {
        return credentials;
    }

    /**
     * Get name of user
     * @return name of user
     */
    @JsonGetter
    public String getName() {
        return this.name;
    }

    /**
     * Get nachname of user
     * @return Nachname of user
     */
    @JsonGetter
    public String getNachname() {
        return this.nachname;
    }

    /**
     * set name of user
     * @param name new Name of user
     */
    @JsonSetter
    public void setName(String name) {
        this.name = name;
    }

    /**
     * set nachnamen
     * @param nachname new nachname
     */
    @JsonSetter
    public void setNachname(String nachname) {
        this.nachname = nachname;
    }
}
