package at.hassmann.objects;

import com.fasterxml.jackson.annotation.*;

/**
 * package contains 4 cards is a extention of cards, shows for 4 Cards the price and package name
 */
@JsonAutoDetect
public class Package extends Cards {
    @JsonProperty
    private String name;
    @JsonProperty
    private final int price;

    /**
     * creates new Package with given stats
     * @param stack Cards
     * @param name Name
     * @param price Price
     */
    @JsonCreator
    public Package(@JsonProperty Cards stack,@JsonProperty String name,@JsonProperty int price) {
        super(stack.getCards());
        this.name = name;
        this.price = price;
    }

    /**
     * get name of package
     * @return Name of Package
     */
    @JsonGetter
    public String getName() {
        return this.name;
    }

    /**
     * get price of package
     * @return Preis of Package
     */
    @JsonGetter
    public int getPrice() {
        return this.price;
    }

    /**
     * set Namen of Package
     * @param neuName new name of Package
     */
    @JsonSetter
    public void setName(String neuName) {
        this.name = neuName;
    }
}
