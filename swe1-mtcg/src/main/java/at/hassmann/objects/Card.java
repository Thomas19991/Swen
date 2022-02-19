package at.hassmann.objects;

import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;
import at.hassmann.server.helper.EnumHelper;
import com.fasterxml.jackson.annotation.*;
import java.util.Objects;

/**
 * Object Card
 */
@JsonAutoDetect
public class Card {
    @JsonProperty
    private String name;
    @JsonProperty
    private double damage;
    @JsonProperty
    private ElementType elementType;
    @JsonProperty
    private final CardType cardType;
    @JsonProperty
    private boolean locked;

    /**
     * create new Card with given properties
     * @param name card name
     * @param damage damage of the card
     * @param elementType ElementType of the card
     * @param cardType CardType of the card
     */
    public Card(String name, double damage, ElementType elementType, CardType cardType) {
        this.name = name;
        this.damage = damage;
        this.elementType = elementType;
        this.cardType = cardType;
    }

    /**
     * Creates a new card with, based on the cardName, the ElementType and the CardType are automatically determined
     * @param name Name der Card (ID)
     * @param elementCardTyp ElementTyp and CardType in String. Both Enums werden chosen trough this string
     * @param damage Damage der Card
     */
    @JsonCreator
    public Card(@JsonProperty("Id") String name, @JsonProperty("Name") String elementCardTyp, @JsonProperty("Damage") double damage){
        CardType tmpCard = EnumHelper.stringToCardType(elementCardTyp);
        ElementType tmpElement = null;
        if(tmpCard == null){
            tmpCard = CardType.UNDEF;
        }

        //Special cards that do not have an element are assigned their natural element here
        switch (tmpCard.name().toLowerCase()) {
            case "dragon":
            case "fireelves":
                tmpElement = ElementType.FIRE;
                break;
            case "ork":
            case "wizzard":
            case "knight":
            case "troll":
            case "goblin":
            case "elf":
                tmpElement = ElementType.REGULAR;
                break;
            case "kraken":
                tmpElement = ElementType.WATER;
                break;
        }

        if(tmpElement == null){
            tmpElement = EnumHelper.stringToElementType(elementCardTyp);
            switch (Objects.requireNonNull(tmpElement).name().toLowerCase()) {
                case "feuer":
                    tmpElement = ElementType.FIRE;
                    break;
                case "wasser":
                    tmpElement = ElementType.WATER;
                    break;
                case "normal":
                    tmpElement = ElementType.REGULAR;
                    break;
            }
        }
        this.name = name;
        this.damage = damage;
        this.elementType = tmpElement;
        this.cardType = tmpCard;
    }

    /**
     * get Name of Card
     * @return Name of Card
     */
    @JsonGetter
    public String getName() {
        return this.name;
    }

    /**
     * get Damage of Card
     * @return Damage
     */
    @JsonGetter
    public double getDamage() {
        return this.damage;
    }

    /**
     * get ElementType of Card
     * @return ElementType of Card
     */
    @JsonGetter
    public ElementType getElementTyp() {
        return this.elementType;
    }

    /**
     * get CardType of Card
     * @return CardType
     */
    @JsonGetter
    public CardType getCardType() {
        return this.cardType;
    }

    /**
     * set Name of Card
     * @param neuerName Name of Card
     */
    @JsonSetter
    public void setName(String neuerName) {
        this.name = neuerName;
    }

    /**
     * set damage of Card
     * @param damage Damage
     */
    @JsonSetter
    public void setDamage(int damage) {
        this.damage = damage;
    }

    /**
     * set ElementType of Card
     * @param elementType ElementType
     */
    @JsonSetter
    public void setElementType(ElementType elementType) {
        this.elementType = elementType;
    }

    /**
     * Testet if cards match
     * @param card Card which should be checken
     * @return True if cards match
     */
    public boolean equals(Card card){
        if(card == null) return false;
        return this.name.equals(card.getName()) && this.cardType == card.getCardType() && this.elementType == card.getElementTyp() && this.damage == card.getDamage();
    }
}
