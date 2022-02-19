package at.hassmann.objects;

import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;
import at.hassmann.server.helper.EnumHelper;
import com.fasterxml.jackson.annotation.*;

/**
 * trading deal is handled here
 */
@JsonAutoDetect
public class TradingDeal {
    @JsonProperty
    private final String id;
    @JsonProperty
    private final String username;
    @JsonProperty
    private Card cardToTrade;
    @JsonProperty
    private ElementType requiredElementType;
    @JsonProperty
    private double requiredMinDamage;
    @JsonProperty
    private CardType requiredCardType;

    /**
     * one TradingDeal created
     * @param id Id of the Trading deal
     * @param cardToTrade Card should be exchanged
     * @param requiredElementType Req ElementType for exchange
     * @param requiredMinDamage Req min damage for exchange
     * @param requiredCardType Req Card Type for exchange
     * @param username Username for deal created
     */
    public TradingDeal(String id, Card cardToTrade, ElementType requiredElementType, double requiredMinDamage, CardType requiredCardType, String username) {
        this.id = id;
        this.cardToTrade = cardToTrade;
        if(requiredElementType == null){
            requiredElementType = ElementType.UNDEF;
        }
        if(requiredCardType == null) {
            requiredCardType = CardType.UNDEF;
        }
        this.requiredElementType = requiredElementType;
        this.requiredMinDamage = requiredMinDamage;
        this.requiredCardType = requiredCardType;
        this.username = username;
    }

    /**
     * TradingDeal gets created, in order of type the cardtype and elementtype is selected automatically
     * @param id Id of Trading deal
     * @param cardToTrade Card which should be swapped
     * @param type ElementTyp and CardType in a String. Both Enums gets chosen through this string
     * Wif no proper Typ found, property stays null
     * @param username Username of TradingDeal created
     * @param requiredMinDamage Required min Damage
     */
    @JsonCreator
    public TradingDeal(@JsonProperty("Id") String id, @JsonProperty("CardToTrade") Card cardToTrade, @JsonProperty("MinimumDamage") double requiredMinDamage, @JsonProperty("Type") String type, String username) {
        this(id, cardToTrade, EnumHelper.stringToElementType(type), requiredMinDamage, EnumHelper.stringToCardType(type), username);
    }

    /**
     * get ID of Trading deal
     * @return Id of Trading Deal
     */
    public String getId() {
        return id;
    }

    /**
     * get Username
     * @return username as String
     */
    @JsonGetter
    public String getUsername() {
        return username;
    }

    /**
     * Card which should be traded
     * @return Card which should be traded
     */
    @JsonGetter
    public Card getCardToTrade() {
        return this.cardToTrade;
    }

    /**
     * Req Element type holen
     * @return Req Element Type
     */
    @JsonGetter
    public ElementType getRequiredElementType() {
        return this.requiredElementType;
    }

    /**
     * get req min damage
     * @return req min damage
     */
    @JsonGetter
    public double getRequiredMinDamage() {
        return this.requiredMinDamage;
    }

    /**
     * get req cardtype
     * @return CardType
     */
    @JsonGetter
    public CardType getRequiredCardType() {
        return this.requiredCardType;
    }

    /**
     * set card which should be traded
     * @param cardToTrade card to trade
     */
    @JsonSetter
    public void setCardToTrade(Card cardToTrade) {
        this.cardToTrade = cardToTrade;
    }

    /**
     * set req elementType
     * @param requiredElementType of Req elementType
     */
    @JsonSetter
    public void setRequiredElementType(ElementType requiredElementType) {
        this.requiredElementType = requiredElementType;
    }

    /**
     * set req min damage
     * @param requiredMinDamage the req min damage
     */
    @JsonSetter
    public void setRequiredMinDamage(double requiredMinDamage) {
        this.requiredMinDamage = requiredMinDamage;
    }

    /**
     * set req cardType
     * @param requiredCardType of Req CardType
     */
    @JsonSetter
    public void setRequiredCardType(CardType requiredCardType) {
        this.requiredCardType = requiredCardType;
    }

    /**
     * from card in parameter gets the Card with requirements from this checked
     * @param checkCard Card which gets checked for the deal
     * @return True when deal ok
     */
    public boolean cardOk(Card checkCard){
        return checkCard.getCardType().equals(this.requiredCardType) && (checkCard.getElementTyp().equals(this.requiredElementType) || this.requiredElementType.name().equalsIgnoreCase("undef")) && checkCard.getDamage() >= this.requiredMinDamage;
    }
}
