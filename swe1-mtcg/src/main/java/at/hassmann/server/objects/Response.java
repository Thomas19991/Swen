package at.hassmann.server.objects;

import at.hassmann.objects.*;
import at.hassmann.objects.Package;
import at.hassmann.server.DBFunctions;
import at.hassmann.server.helper.JsonHelper;
import at.hassmann.server.helper.PostgresHelper;
import at.hassmann.server.helper.ResponseHelper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.*;

/**
 * creates and sends a Response with the Requests
 */
public class Response {
    private final PrintStream out;
    private final String url;
    private final String loadInput;
    private final String authUserString;

    /**
     * gets the Data of the requests and generates a Response
     * @param url Request Url
     * @param command Request CMD
     * @param out out Print Stream
     * @param authUserString the MsgHandler
     * @param loadInput input of the Request
     */
    public Response(String url, String command, PrintStream out, String authUserString, String loadInput){
        this.authUserString = authUserString;
        this.url = url;
        this.out = out;
        this.loadInput = loadInput;
        if (this.url != null) {
            switch (command) {
                case "GET":
                    if (login()) {     // -> auth check
                        getMethodes();
                    } else {
                        sendResponse("Login Error", "401");
                    }
                    break;
                case "POST":
                    try {
                        postMethodes();
                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                    break;
                case "PUT":
                    if (login()) {
                        putMethodes();
                    } else {
                        sendResponse("Login Error", "401");
                    }
                    break;
                case "DELETE":
                        deleteMethodes();
                    break;
                default:
                    sendResponse(command + " not found!", "405");
                    break;
            }
        }
    }

    /**
     * all Get Methods
     * only for auth users
     */
    private void getMethodes(){
        if (this.url.startsWith("/users")) {
            String username = this.url.substring(this.url.lastIndexOf('/') + 1);
            User user = new DBFunctions().getUser(username);
            if (username.equalsIgnoreCase(authUserString)){
                String userJson = JsonHelper.userToJson(user);
                if(userJson != null && !userJson.isEmpty()){
                    sendResponse(userJson, "200");
                }
            }else{
                sendResponse("Wrong credentials", "500");
            }
        } else if (this.url.startsWith("/cards")) {
            Cards allCards = new DBFunctions().getCards(this.authUserString);
            String jsonCards = JsonHelper.objToJson(allCards);
            if (jsonCards != null && !jsonCards.isEmpty()){
                sendResponse(jsonCards, "200");
            }else{
                sendResponse("Cards Json error", "500");
            }
        }else if(this.url.startsWith("/deck")) {
            String format = this.url.substring(this.url.lastIndexOf('?') + 1);
            ArrayList<String> allCards = new DBFunctions().getDeck(this.authUserString);
            Cards deck;
            Object obj;
            if(format.startsWith("format=plain")){
                obj = allCards;
            }else{
                deck = new DBFunctions().getCardsFromIDList(allCards);
                obj = deck;
            }
            String jsonCards = JsonHelper.objToJson(obj);
            if (jsonCards != null && !jsonCards.isEmpty()) {
                sendResponse(jsonCards, "200");
            } else {
                sendResponse("", "500");
            }
        }else if(this.url.startsWith("/tradings")) {
            ArrayList<TradingDeal> allDeals = new DBFunctions().getAllTradingDeals();
            if(allDeals != null && !allDeals.isEmpty()){
                String json = JsonHelper.objToJson(allDeals);
                if(json != null && !json.equals("")){
                    sendResponse(json, "200");
                }else{
                    sendResponse("Trading Deals to Json error", "500");
                }
            }else{
                sendResponse("Keine Trading Deals gefunden", "500");
            }
        }else if(this.url.startsWith("/score")) {
            String username = this.authUserString;
            if (username != null && !username.isEmpty()){
                int lastBallteId = new DBFunctions().getLastBattleIdUser(username);
                if (lastBallteId > -1){
                    Map<String, String> map = new DBFunctions().getBattleLog(lastBallteId + "");
                    if(map != null && !map.isEmpty()){
                        sendResponse("BATTLE #" + map.get("id") + "\nSCORE\n" + map.get("playerone") + "(#Player1) |vs| " + map.get("playertwo") + "(#Player2) \n" + map.get("playeronescore") + "(#Player1) |vs| " + map.get("playertwoscore") + "(#Player2) \nGame LOG:\n" + ResponseHelper.logLineBreak(map.get("log")), "200");
                    }else {
                        sendResponse("Konnte Battle log nicht holen", "500");
                    }
                }else {
                    sendResponse("Last Battle ID error", "500");
                }

            }else{
                sendResponse("Login Error", "401");
            }
        }else if(this.url.startsWith("/stats")) {
            String username = this.authUserString;
            if (username != null && !username.isEmpty()) {
                ArrayList<String> battleIds = new DBFunctions().getAllBattleIdUser(username);
                if (battleIds != null && !battleIds.isEmpty()){
                    StringBuilder resString = new StringBuilder();
                    for(String i : battleIds){
                        Map<String, String> map = new DBFunctions().getBattleLog(i + "");
                        if(map != null && !map.isEmpty()){
                            resString = new StringBuilder("BATTLE #" + map.get("id") + "\nSCORE\n" + map.get("playerone") + "(#Player1) |vs| " + map.get("playertwo") + "(#Player2) \n" + map.get("playeronescore") + "(#Player1) |vs| " + map.get("playertwoscore") + "(#Player2) \nGame LOG:\n");
                            resString.append(ResponseHelper.logLineBreak(map.get("log")));     //loglinebreak in response helper
                        }else {
                            sendResponse("Error: Kann Battle log nicht holen", "500");
                        }
                    }
                    sendResponse(resString.toString(), "200");
                }else {
                    sendResponse("No battles done yet", "500");
                }
            }else{
                sendResponse("Login Error", "401");
            }
        }else{
            sendResponse(this.url + " not found!", "404");
        }
    }

    /**
     * all post methods
     * can also be reached by no logged in users
     */
    private void postMethodes() throws JsonProcessingException {
        if (this.url.startsWith("/users")) {
            Map<String, Object> map = JsonHelper.jsonInputLoadloadToMap(this.loadInput);
            String username = (String) Objects.requireNonNull(map).get("Username");
            String password = (String) map.get("Password");
            User newUser = new User(new Credentials(username, password), username, username, new Coins(20), "BIO", "IMAGE");
            DBFunctions con = new DBFunctions();
            if(!con.addUser(newUser.getCredentials().getUsername(), newUser.getCredentials().getPasswort(), newUser.getCredentials().getUsername(), newUser.getBio(), newUser.getImage())){
                sendResponse("ERROR: User already exists!", "409");
            }
            String userJson = JsonHelper.userToJson(newUser);
            if(userJson != null) {
                sendResponse(userJson + "\nUser added", "201");
            }else{
                sendResponse("Error creating user", "500");
            }
        }else if (this.url.startsWith("/sessions")) {
            Map<String, Object> map = JsonHelper.jsonInputLoadloadToMap(this.loadInput);
            String username = (String) Objects.requireNonNull(map).get("Username");
            String password = (String) map.get("Password");
            if(loginWithPW(username, password)){
                sendResponse(username + " was sucessfully logged in", "200");
            }else{
                sendResponse("Error occured", "401");
            }
        }else if (this.url.startsWith("/packages")) {
            if (login()) {
                ObjectMapper objectMapper = new ObjectMapper();
                ArrayList<Card> listCards = objectMapper.readValue(this.loadInput, new TypeReference<>() {});
                Package packageCards = new Package(new Cards(listCards), new DBFunctions().nextPackageId() + "", 5);
                if (!new DBFunctions().addPackage(packageCards)) {
                    sendResponse("Package add error", "500");
                } else {
                    String packageJson = JsonHelper.objToJson(packageCards);
                    if (packageJson != null) {
                        sendResponse(packageJson + "\nPackage successfully added", "201");
                    } else {
                        sendResponse("Package error", "500");
                    }
                }
            } else {
                sendResponse("Login Error", "401");
            }
        }else if (this.url.startsWith("/transactions/packages")) {
            if (login()) {
                DBFunctions db = new DBFunctions();
                int coins = new DBFunctions().checkCoins(this.authUserString);
                if (!(coins - 5 >= 0)) {
                    sendResponse("Nur " + coins + " von 5 coins vorhanden", "500");
                }else {
                    Package newPackage = db.userAcquirePackage(this.authUserString);
                    if (newPackage == null) {
                        sendResponse("Kein Package vorhanden", "500");
                    } else {
                        String packageJson = JsonHelper.objToJson(newPackage);
                        if (packageJson == null) {
                            sendResponse("Package Json error", "500");
                        } else {
                            if(!new DBFunctions().updateCoins(coins - 5, this.authUserString)){
                                sendResponse("User coins konnten nicht gesetzt werden", "500");
                            }
                            sendResponse(packageJson, "200");
                        }
                    }
                }
            }else{
                sendResponse("Login Error", "401");
            }
        }else if (this.url.startsWith("/tradings")) {
            if(login()) {
                String dotradeid = this.url.substring(this.url.lastIndexOf('/') + 1);
                if (!dotradeid.isEmpty() && !dotradeid.equals("tradings")) {
                    //trading
                    String username = this.authUserString;
                    if (username != null && !username.isEmpty()) {
                        ArrayList<TradingDeal> tradingDealArrayList = new DBFunctions().getAllTradingDeals();
                        TradingDeal tradingDeal = null;
                        if (tradingDealArrayList != null && !tradingDealArrayList.isEmpty()) {
                            for (TradingDeal tr : tradingDealArrayList) {
                                if (tr.getId().equals(dotradeid)) {
                                    tradingDeal = tr;
                                }
                            }
                            Card card = new DBFunctions().getCardFromID(this.loadInput);
                            if (card != null) {
                                if (tradingDeal != null) {
                                    if (!tradingDeal.getUsername().equals(username)){
                                        if (tradingDeal.cardOk(card)) {
                                            String json = JsonHelper.objToJson(card);
                                            if (json != null && !json.isEmpty()) {
                                                if (new DBFunctions().addUserCard(username, tradingDeal.getCardToTrade().getName())) {
                                                    if (new DBFunctions().delUserCard(tradingDeal.getUsername(), tradingDeal.getCardToTrade().getName())) {
                                                        if (new DBFunctions().deleteTradingDeal(tradingDeal.getId())) {
                                                            if (new DBFunctions().delUserCard(username, card.getName())) {
                                                                if (new DBFunctions().addUserCard(tradingDeal.getUsername(), card.getName())) {
                                                                    sendResponse(json, "200");
                                                                } else {
                                                                    sendResponse("ERROR --> Add Card to: " + tradingDeal.getUsername(), "500");
                                                                }
                                                            } else {
                                                                sendResponse("ERROR --> Del Card from: " + username, "500");
                                                            }
                                                        } else {
                                                            sendResponse("Error --> Del Trading Deal", "500");
                                                        }
                                                    } else {
                                                        sendResponse("ERROR --> Del Card from: " + tradingDeal.getUsername(), "500");
                                                    }
                                                } else {
                                                    sendResponse("ERROR --> Add Card to: " + username, "500");
                                                }
                                            } else {
                                                sendResponse("ERROR --> JSON Empty", "500");
                                            }
                                        } else {
                                            sendResponse("ERROR --> Trading Deal not ok", "500");
                                        }
                                    }else {
                                        sendResponse("ERROR --> Kann nicht mit sich selbst traden", "500");
                                    }
                                } else {
                                    sendResponse("ERROR --> Trading Deal not exist", "500");
                                }
                            } else {
                                sendResponse("ERROR --> Card not exist", "500");
                            }
                        } else {
                            sendResponse("ERROR --> Trading Deal not exist", "500");
                        }
                    } else {
                        sendResponse("ERROR --> Username empty", "401");
                    }
                } else {
                    //CREATE TRADING DEAL
                    Map<String, Object> map = JsonHelper.jsonInputLoadloadToMap(this.loadInput);
                    String id = (String) Objects.requireNonNull(map).get("Id");
                    String cardtotrade = (String) map.get("CardToTrade");
                    String type = (String) map.get("Type");
                    double mindamage = Double.parseDouble(map.get("MinimumDamage") + "");
                    String username = this.authUserString;
                    if (username != null) {
                        Card cardtoTradeC = new DBFunctions().getCardFromID(cardtotrade);
                        if (cardtoTradeC != null) {
                            TradingDeal tradingDeal = new TradingDeal(id, cardtoTradeC, mindamage, type, username);
                            String tradingJson = JsonHelper.objToJson(tradingDeal);
                            ArrayList<String> deckCards = new DBFunctions().getDeck(username);
                            if (deckCards != null) {
                                if (deckCards.contains(cardtotrade)) {
                                    new DBFunctions().delDeck(username);
                                }
                                if (new DBFunctions().addTradingdeal(tradingDeal.getUsername(), tradingDeal.getId(), tradingDeal.getRequiredMinDamage(), tradingDeal.getRequiredCardType().name(), tradingDeal.getRequiredElementType().name(), tradingDeal.getCardToTrade().getName())) {
                                    if (new DBFunctions().updateCardLock(tradingDeal.getCardToTrade().getName(), true)) {
                                        sendResponse(Objects.requireNonNull(JsonHelper.objToJson(tradingDeal)), "201");
                                    } else {
                                        sendResponse("", "500");
                                    }
                                } else {
                                    sendResponse("", "500");
                                }
                            } else {
                                sendResponse("", "500");
                            }
                            sendResponse(Objects.requireNonNull(tradingJson), "201");
                        } else {
                            sendResponse("", "500");
                        }
                    } else {
                        sendResponse("", "500");
                    }
                }
            }
        }else if (this.url.startsWith("/battle")) {
            if(login()){
                String username = this.authUserString;
                if (username != null && !username.isEmpty()) {
                    List<String> deckNames = new DBFunctions().getDeck(username);
                    if (deckNames != null && !deckNames.isEmpty()) {
                        Cards deck = new DBFunctions().getCardsFromIDList(deckNames);
                        if(deck != null && deck.getCards().size() == 4) {
                            Battle openBattle = new DBFunctions().getOpenBattle();
                            if (openBattle == null) {
                                //Creator player Mode
                                if(new DBFunctions().addBattle(username)){
                                    sendResponse("Du bist: ->PLAYER 1\nBattle Einladung wurde erstellt von: " + username + "(->PLAYER1) \nSobald ein 2. Spieler dem Battle beitritt, kann das Ergebnis mit /score abgefragt werden.","200");
                                }else {
                                    sendResponse("Something wrong", "500");
                                }
                            } else {
                                User player2 = new DBFunctions().getUser(username);  //Join game player
                                if(player2 != null){
                                    openBattle.setPlayer2(player2);
                                    openBattle.setDeckPlayer2(deck);
                                    if(new DBFunctions().delBattleInvitation(openBattle.getId() + "")) {
                                        if (openBattle.doFight()){
                                            if (new DBFunctions().addBattleLog(openBattle.getId() + "", openBattle.getPlayer1().getName(), openBattle.getPlayer2().getName(), openBattle.getScorePlayer1() + "", openBattle.getScorePlayer2() + "", openBattle.getLog().toString())) {
                                                if (new DBFunctions().delDeck(openBattle.getPlayer1().getCredentials().getUsername()) && new DBFunctions().delDeck(openBattle.getPlayer2().getCredentials().getUsername())) {
                                                    //DEL OLD DECK CARDS
                                                    ArrayList<String> oldDeck1 = new ArrayList<>();
                                                    for (Card ca : openBattle.getDeckPlayer1Init().getCards()) {
                                                        oldDeck1.add(ca.getName());
                                                    }
                                                    ArrayList<String> oldDeck2 = new ArrayList<>();
                                                    for (Card ca : openBattle.getDeckPlayer2Init().getCards()) {
                                                        oldDeck2.add(ca.getName());
                                                    }
                                                    //DEL NEW CARDS IF EXIST
                                                    Cards player1cards = new DBFunctions().getCards(openBattle.getPlayer1().getCredentials().getUsername());
                                                    for (Card ca : openBattle.getDeckPlayer1().getCards()) {
                                                        oldDeck1.add(ca.getName());
                                                    }
                                                    if (player1cards.getCards() != null && !player1cards.getCards().isEmpty()) {
                                                        for (String ca : oldDeck1) {
                                                            if (!new DBFunctions().delUserCard(openBattle.getPlayer1().getCredentials().getUsername(), ca)) {
                                                                sendResponse("Error Deleting User card1: " + ca, "500");
                                                            }
                                                        }
                                                    }
                                                    Cards player2cards = new DBFunctions().getCards(openBattle.getPlayer2().getCredentials().getUsername());
                                                    for (Card ca : openBattle.getDeckPlayer2().getCards()) {
                                                        oldDeck2.add(ca.getName());
                                                    }
                                                    if (player2cards.getCards() != null && !player2cards.getCards().isEmpty()) {
                                                        for (String ca : oldDeck2) {
                                                            if (!new DBFunctions().delUserCard(openBattle.getPlayer2().getCredentials().getUsername(), ca)) {
                                                                sendResponse("Error Deleting User card2: " + ca, "500");
                                                            }
                                                        }
                                                    }
                                                    //add cards to deck
                                                    for (Card ca : openBattle.getDeckPlayer1().getCards()) {
                                                        if (!new DBFunctions().addUserCard(openBattle.getPlayer1().getCredentials().getUsername(), ca.getName())) {
                                                            sendResponse("Error adding card to user1: " + ca.getName(), "500");
                                                        }
                                                    }
                                                    for (Card ca : openBattle.getDeckPlayer2().getCards()) {
                                                        if (!new DBFunctions().addUserCard(openBattle.getPlayer2().getCredentials().getUsername(), ca.getName())) {
                                                            sendResponse("Error adding card to user2: " + ca.getName(), "500");
                                                        }
                                                    }
                                                    sendResponse("Du bist: #PLAYER 2\nBattle --> " + openBattle.getPlayer1().getName() + "(#PLAYER1) |vs| " + openBattle.getPlayer2().getName() + "(#PLAYER2)\nErgebnisse unter /score abrufbar", "200");
                                                }
                                            } else {
                                                sendResponse("Battle Log konnte nicht geschrieben werden", "500"); //ERROR
                                            }
                                        }else {
                                            sendResponse("Battle konnte nicht durchgeführt werden", "500");
                                        }
                                    }else{
                                        sendResponse("Battle Einladung konnte nicht akzeptiert werden", "500"); //ERROR
                                    }
                                }else{
                                    sendResponse("GET User error", "500"); //ERROR
                                }
                            }
                        }else {
                            sendResponse("Nur "+ Objects.requireNonNull(deck).getCards().size()+" von 4 Karten im Deck. \nMach zuerst POST /deck [ID, ID, ID, ID] um dein Deck korrekt zu befüllen","424");
                        }
                    }else {
                        sendResponse("Deck ist nicht gesetzt","424");
                    }
                }else {
                    sendResponse("", "500");
                }
            }else {
                sendResponse("Login Error", "401");
            }
        } else{
            sendResponse(this.url + " not found!", "404");
        }

    }

    /**
     * checks login with username
     * @return True if login works, else false
     */
    private boolean login(){
        if(this.authUserString != null && !this.authUserString.isEmpty()){
            String username = this.authUserString;
            User user = new DBFunctions().getUser(username);
            return username.equals(user.getCredentials().getUsername());
        }else{
            return false;
        }
    }

    /**
     * checks login with username and password
     * @return True if login data right, else false
     */
    private boolean loginWithPW(String username, String password){
        if(username != null && password !=null){
            User user = new DBFunctions().getUser(username);
            return (user.getCredentials().getUsername().equals(username) && user.getCredentials().getPasswort().equals(password));
        }
        return false;
    }

    /**
     * all put methods
     * only for auth users reachable
     */
    private void putMethodes(){
        if (this.url.startsWith("/users")) {
            String username = this.url.substring(this.url.lastIndexOf('/') + 1);
            User user = new DBFunctions().getUser(username);
            if(username.equalsIgnoreCase(authUserString)) {
                Map<String, Object> map = JsonHelper.jsonInputLoadloadToMap(this.loadInput);
                String bio = (String) Objects.requireNonNull(map).get("Bio");
                String image = (String) map.get("Image");
                String name = (String) map.get("Name");
                user.setBio(bio);
                user.setImage(image);
                user.setNachname(name);
                if (new DBFunctions().updateUser(username, user.getBio(), user.getImage(), user.getNachname())) {
                    sendResponse(JsonHelper.userToJson(user) + "\nUser updated","200");
                } else {
                    sendResponse("Failed", "500");
                }
            }else{
                sendResponse("Wrong credentials", "500");
            }
        }else if(this.url.startsWith("/deck")) {
            List<String> deckIds = JsonHelper.jsonInputLoadToList(this.loadInput);
            if (deckIds != null && deckIds.size() == 4){
                if (new DBFunctions().setDeck(this.authUserString, deckIds)){
                    Cards deck = new DBFunctions().getCardsFromIDList(deckIds);
                    String deckJson = JsonHelper.objToJson(deck);
                    if (deck != null && deckJson != null){
                        sendResponse(deckJson, "200");
                    }else {
                        sendResponse("Error: Deck konnte nicht aus der DB geholt werden", "500");
                    }
                }else {
                    sendResponse("Error: Deck konnte nicht gesetzt werden", "500");
                }
            }else{
                sendResponse("ERROR: Nur " + Objects.requireNonNull(deckIds).size() + " von 4 Karten sind im Deck.","500");
            }
        }else{
            sendResponse(this.url + " not found!", "404");
        }
    }

    /**
     * all delete methods
     */
    private void deleteMethodes(){
        if (this.url.startsWith("/tradings")) {
            if(login()) {
                String tradeId = this.url.substring(this.url.lastIndexOf('/') + 1);
                ArrayList<TradingDeal> allTradingDeals = new DBFunctions().getAllTradingDeals();
                TradingDeal td = null;
                if (allTradingDeals != null && !allTradingDeals.isEmpty()) {
                    for (TradingDeal i : allTradingDeals) {
                        if (i.getId().equals(tradeId)) {
                            td = i;
                        }
                    }
                    if (td != null) {
                        if (new DBFunctions().deleteTradingDeal(tradeId)) {
                            if (new DBFunctions().updateCardLock(td.getCardToTrade().getName(), false))
                                sendResponse("worked", "204");
                        } else {
                            sendResponse("failed", "500");
                        }
                    } else {
                        sendResponse("failed", "500");
                    }
                }
            }else{
                sendResponse("Login Error", "401");
            }
        }else if (this.url.startsWith("/db/del/all")) {
            if(this.authUserString.equals("kienboec")) {
                try {
                    Connection c = PostgresHelper.con();
                    PreparedStatement ps = c.prepareStatement("drop table user_deck; drop table trading; drop table battle; drop table battle_log; drop table user_cards; drop table package; drop table card; drop table users;");
                    ps.executeUpdate();
                    ps.close();
                    c.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                    sendResponse("DB drop error", "500");
                }
                // if(PostgresHelper.executeUpdate("drop table user_deck; drop table trading; drop table battle; drop table battle_log; drop table user_cards; drop table package; drop table card; drop table users;")) {
                if (!new DBFunctions().initial()) {
                    sendResponse("DB init failed", "500");
                } else {
                    sendResponse("DB deleted & recreated", "205");
                }
            } else {
                sendResponse("Login Error", "401");
            }
        } else {
            sendResponse(this.url + " not found!", "404");
        }
    }

    /**
     * Sends a Response
     * @param responseText Text to send
     * @param code Http code
     */
    private void sendResponse(String responseText, String code){
        out.print("HTTP/1.0 "+code+"\r\n");
        out.print("Server: Apache/0.8.4\r\n");
        out.print("Content-Type: text/plain\r\n");
        out.print("Content-Length: "+responseText.length()+"\r\n");
        out.print("\r\n");
        out.print(responseText);
    }
}
