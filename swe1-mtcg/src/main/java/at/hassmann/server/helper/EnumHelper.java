package at.hassmann.server.helper;

import at.hassmann.enums.CardType;
import at.hassmann.enums.ElementType;

/**
 * to manipulate the enums
 */
public class EnumHelper {

    /**
     * if string as ElementType there, then it gives the right elementtype back
     * @param elementTypeString elementType as String
     * @return right ElementType or null if not found
     */
    public static ElementType stringToElementType(String elementTypeString){
        for (ElementType et : ElementType.values()) {
            if (elementTypeString.toLowerCase().contains(et.name().toLowerCase())) {
                return et;
            }
        }
        return null;
    }

    /**
     * if String as cardType there, then it gives the right cardtype back
     * @param cardTypeString cardType as String
     * @return Passender Card Type or null if nothing found
     */
    public static CardType stringToCardType(String cardTypeString){
        for (CardType ct : CardType.values()) {
            if (cardTypeString.toLowerCase().contains(ct.toString().toLowerCase())) {
                return ct;
            }
        }
        return null;
    }
}
