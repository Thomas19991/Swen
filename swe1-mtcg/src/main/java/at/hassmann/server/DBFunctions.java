package at.hassmann.server;

import at.hassmann.objects.*;
import at.hassmann.objects.Package;
import at.hassmann.server.helper.JsonHelper;
import at.hassmann.server.helper.PostgresHelper;
import java.sql.*;
import java.util.ArrayList;
import java.util.Collections;
import java.sql.PreparedStatement;
import java.util.List;
import java.util.Map;

/**
 * provides db connection
 */
public class DBFunctions {
    private Connection c;
    public DBFunctions() {
        PostgresHelper.con();
    }

    /**
     * creates tables
     * @return True when success, else error
     */
    public boolean initial() {
        ArrayList<Boolean> errors = new ArrayList<>();
        errors.add(PostgresHelper.executeUpdateMessage("CREATE TABLE IF NOT EXISTS USERS (username TEXT PRIMARY KEY NOT NULL, nachname TEXT NOT NULL, password TEXT NOT NULL, bio TEXT, image TEXT, coins integer default 20 not null)", "User Table created"));
        errors.add(PostgresHelper.executeUpdate("CREATE TABLE IF NOT EXISTS CARD(NAME TEXT not null,DAMAGE FLOAT not null,ELEMENTTYP TEXT not null,CARDTYPE TEXT not null, PRIMARY KEY (\"name\"));"));
        errors.add(PostgresHelper.executeUpdateMessage("create unique index IF NOT EXISTS card_name_uindex on CARD (NAME);", "Card Table created"));
        errors.add(PostgresHelper.executeUpdateMessage("create table IF NOT EXISTS package(\"ID\" varchar(255) not null,name varchar(255) not null constraint name references card, i serial not null constraint package_i primary key );", "Package Table created"));
        errors.add(PostgresHelper.executeUpdateMessage("create table IF NOT EXISTS user_cards(username TEXT not null constraint user_cards_users_username_fk references users,name text not null, gesperrt boolean not null);", "UserCards Table created"));
        errors.add(PostgresHelper.executeUpdateMessage("create table IF NOT EXISTS user_deck(username text not null constraint user_deck_users_username_fk references users,cardname text not null);", "UserDeck Table created"));
        errors.add(PostgresHelper.executeUpdateMessage("create table IF NOT EXISTS trading(username text not null constraint trading_users_username_fk references users,id text not null constraint trading_pk primary key, cardtotrade text not null constraint trading_card_name_fk references card, mindamage float not null,reqcardtype text not null,reqelement text not null);", "Trading Table created"));
        errors.add(PostgresHelper.executeUpdate("create table if not exists battle(usernamecreator text not null constraint battle_users_username_fk references users,usernameplayer text constraint battle_users_username_fk_2 references users, battleid serial, deckcreator text not null);"));
        errors.add(PostgresHelper.executeUpdateMessage("create unique index if not exists battle_battleid_uindex on battle (battleid);", "Battle Table created"));
        errors.add(PostgresHelper.executeUpdate("create table IF NOT EXISTS battle_log(id int not null constraint battle_log_pk primary key, playerone text not null,playertwo text not null,playeronescore text not null,playertwoscore text not null,log varchar(10485760));"));
        errors.add(PostgresHelper.executeUpdateMessage("create unique index IF NOT EXISTS battle_log_id_uindex on battle_log (id);", "Battle_lgo Table created"));
        return !errors.contains(false);
    }

    /**
     * gets all BattleIds from the user
     * @param username user to proof
     * @return Null if error, else list of id's
     */
    //public ArrayList<String> getAllBattleIdUser(String username){
        public ArrayList<Integer> getAllBattleIdUser(String username){
        int id;
        ArrayList<Integer> battleIds = new ArrayList<>();
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select id from battle_log where playerone = ? or playertwo = ?;");
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
                if (id > 0) {
                    battleIds.add(id);
                }else {
                    return null;
                }
            }
            rs.close();
            ps.close();
            c.close();
            return battleIds;  //gives ArrayList back
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
    }

    /**
     * gets last BattleId from user
     * @param username Userid which should be checked
     * @return -1 if error, else the last battleid of the user
     */
    public int getLastBattleIdUser(String username){
        int id;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select max(id) from battle_log where playerone = ? or playertwo = ?;");
            ps.setString(1, username);
            ps.setString(2, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("max");  //user with highest id
                if (id > 0) {
                    return id;
                }else {
                    return -1;
                }
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
        return -1;
    }

    /**
     * gets Battlelog of a certain battle
     * @param battleId Id of the battle
     * @return a map with names of player1 and player2, player1Score and player2Score and log
     */
    public Map<String, String> getBattleLog(int battleId){
        int id;
        String playerone, playertwo, score1, score2, log;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select * from battle_log where id = ?;");
            ps.setInt(1, battleId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getInt("id");
                playerone = rs.getString("playerone");
                playertwo = rs.getString("playertwo");
                score1 = rs.getString("playeronescore");
                score2 = rs.getString("playertwoscore");
                log = rs.getString("log");
                if (id > 0 && !playerone.isEmpty() && !playertwo.isEmpty() && !score1.isEmpty() && !score2.isEmpty() && !log.isEmpty()){
                    Map<String, String> map = new java.util.HashMap<>(Collections.emptyMap());
                    map.put("playerone", playerone);
                    map.put("playertwo", playertwo);
                    map.put("playeronescore", score1);
                    map.put("playertwoscore", score2);
                    map.put("log", log);
                    map.put("id", id+"");
                    return map;    //put all data in the map and return it
                }else{
                    return null;
                }
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return null;
    }

    /**
     * puts the battle into battle table
     * @param usernamecreator Username of the creator of the battle
     * @return True if success, else false
     */
    public boolean addBattle(String usernamecreator){
        ArrayList<String> decknamen = getDeck(usernamecreator);
        if(decknamen != null && !decknamen.isEmpty()){
            Cards deck = getCardsFromIDList(decknamen);
            if(deck != null && !deck.getCards().isEmpty()){
                String deckJson = JsonHelper.objToJson(deck.getCards());
                if (deckJson != null && !deckJson.isEmpty()){
                    try {
                        Connection c = PostgresHelper.con();
                        PreparedStatement ps = c.prepareStatement("insert into battle (usernamecreator, deckcreator) VALUES (?,?);");
                        ps.setString(1, usernamecreator);
                        ps.setString(2, deckJson);
                        ps.executeUpdate();
                        ps.close();
                        c.close();
                        return true;
                    } catch (SQLException e) {
                        e.printStackTrace();
                        return false;
                    }
                }else {
                    return false;
                }
            }else {
                return false;
            }
        }else{
            return false;
        }
    }

    /**
     * search for a free open battle in the DB and deletes the open battle
     * @return null if a error occurs or no free battle is available
     */
    public Battle getOpenBattle(){
        try {
            int battleId;
            String username;
            Statement st;
            this.c = PostgresHelper.con();
            st = this.c.createStatement();
            ResultSet rs = st.executeQuery("select * from battle limit 1;");
            while(rs.next()) {
                username = rs.getString("usernamecreator");
                battleId = rs.getInt("battleid");
                User player1 = new DBFunctions().getUser(username);
                if(player1 != null){
                    ArrayList<String> deckPlayer1Arr = new DBFunctions().getDeck(username);
                    if (deckPlayer1Arr != null){
                        Cards deckPlayer1 = new DBFunctions().getCardsFromIDList(deckPlayer1Arr);
                        if(deckPlayer1 != null){
                            if(delBattleInvitation(battleId)){   //deletes battle id so battle is closed
                                return new Battle(battleId, player1, deckPlayer1);   //battle initial
                            }else{
                                return null;
                            }
                        }else{
                            return null;
                        }
                    }else {
                        return null;
                    }
                }else{
                    return null;
                }
            }
            rs.close();
            st.close();
            c.close();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return null;
        }
        return null;
    }

    /**
     * deletes battle inventions of a single player
     * @param battleid id to delete
     * @return true if success, else false
     */
    //public boolean delBattleInvitation(String battleid) {
        public boolean delBattleInvitation(int battleid) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("delete from battle where battleid = ?;");
            ps.setInt(1, battleid);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * After battle the log is set, therefore the same id as in the battle is used
     * @param id Id of Battle
     * @param playerone Name of Player 1
     * @param playertwo Name of Player 2
     * @param playeronescore Score of Player 1
     * @param playertwoscore Score of Player 2
     * @param log Log of Battle
     * @return true if success, else false
     */
    //public boolean addBattleLog(String id, String playerone, String playertwo, int playeronescore, int playertwoscore, String log) {
    public boolean addBattleLog(int id, String playerone, String playertwo, int playeronescore, int playertwoscore, String log) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("insert into battle_log (id, playerone, playertwo, playeronescore, playertwoscore, log) VALUES (?,?,?,?,?,?);");
            ps.setInt(1, id);
            ps.setString(2, playerone);
            ps.setString(3, playertwo);
            ps.setInt(4, playeronescore);
            ps.setInt(5, playertwoscore);
            ps.setString(6, log);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Updates the lock of user card
     * @param name name of card
     * @param lock state of the lock
     * @return true if success, else false
     */
    public boolean updateCardLock(String name, boolean lock) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("Update user_cards set gesperrt = ? where name = ?;");
            ps.setBoolean(1, lock);
            ps.setString(2, name);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * checks if a card is locked for this deck
     * @param name Name of Card to check
     * @return True if locked, else false
     * @throws SQLException Locked Cards can't get fetched out of db
     */
    public boolean getCardLock(String name) throws SQLException {
        boolean locked = false;
        Connection c = PostgresHelper.con();
        PreparedStatement ps = c.prepareStatement("select gesperrt from user_cards where name = ?;");
        ps.setString(1, name);
        ResultSet rs = ps.executeQuery();
        while (rs.next()) {
            locked = rs.getBoolean("gesperrt");
        }
        rs.close();
        ps.close();
        c.close();
        return locked;
    }

    /**
     * Deletes trading deal from db
     * @param id - id of trading deal to delete
     * @return True if success, else false
     */
    public boolean deleteTradingDeal(String id) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("delete from trading where id = ?;");
            ps.setString(1, id);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Puts a trading deal into db
     * @param username User added to trades
     * @param id Id of Trade
     * @param mindamage Min damage thats needed for the trade
     * @param reqcardtype Req Card Type thats needed for the trade
     * @param reqelement Req Element Typ thats needed for the trade
     * @param cardtotrade Card to trade
     * @return True if success, else false
     */
    public boolean addTradingDeal(String username, String id, double mindamage, String reqcardtype, String reqelement, String cardtotrade) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("INSERT INTO trading (username, id, cardtotrade, mindamage, reqcardtype, reqelement) VALUES (?,?,?,?,?,?);");
            ps.setString(1, username);
            ps.setString(2, id);
            ps.setString(3, cardtotrade);
            ps.setDouble(4, mindamage);
            ps.setString(5, reqcardtype);
            ps.setString(6, reqelement);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException ignore) {
            //e.printStackTrace();
            System.out.println("Values already exist");
        }
        return false;
    }

    /**
     * Get all deals from the DB
     * @return Null if error
     */
    public ArrayList<TradingDeal> getAllTradingDeals(){
        ArrayList<TradingDeal> tradingDeals = new ArrayList<>();
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select * from trading;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                String username, id, cardtotrade, reqcardtype, reqelement;
                int mindamage;
                username = rs.getString("username");
                id = rs.getString("id");
                cardtotrade = rs.getString("cardtotrade");
                reqcardtype = rs.getString("reqcardtype");
                reqelement = rs.getString("reqelement");
                mindamage = rs.getInt("mindamage");

                Card card = new DBFunctions().getCardFromID(cardtotrade);
                if(card != null){
                    TradingDeal tmp = new TradingDeal(id, card, mindamage, reqcardtype +reqelement, username);
                    tradingDeals.add(tmp);
                }else{
                    return null;
                }
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return tradingDeals;
    }

    /**
     * Set deck of user
     * @param username from which the deck should be set
     * @param deck id's from the deck in from of an arrayList
     * @return True if success, else false
     */
    public boolean setDeck(String username, List<String> deck){
        for (String st : deck) {
            try {
                if(getCardLock(st)){
                    return false;
                }
            } catch (SQLException throwables) {
                System.err.println(throwables.getMessage());
                return false;
            }
        }
        Cards allCards = getCards(username);
        Cards deckCards = new Cards(new ArrayList<>());
        int count = 0;
        if(allCards != null && deck.size() == 4){
            for (String st : deck) {
                for (Card ca: allCards.getCards()) {
                    if(ca.getName().equals(st) && count < 4){
                        if(deckCards.getCards().size() == 0){
                            if (!delDeck(username)){
                                return false;
                            }
                        }
                        deckCards.addCard(ca);
                        if(deckCards.getCards().size() == 4){
                            int x = 0;
                            for(Card cardtmp : deckCards.getCards()){
                                x++;
                                try {
                                    Connection c = PostgresHelper.con();
                                    PreparedStatement ps = c.prepareStatement("INSERT INTO user_deck (username, cardname) VALUES (?,?);");
                                    ps.setString(1, username);
                                    ps.setString(2, cardtmp.getName());
                                    ps.executeUpdate();
                                    ps.close();
                                    c.close();
                                    System.out.println("Card #"+x+" added to Deck");
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                    return false;
                                }
                            }
                            return true;
                        }
                    }
                }
                count++;
            }
            return false;
        }
        return false;
    }

    /**
     * Gets all card names out of the users deck
     * @param username user from which the deck names should be get
     * @return null is error
     */
    public ArrayList<String> getDeck(String username){
        ArrayList<String> cardNameArray = new ArrayList<>();
        String cardname;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select * from user_deck where username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cardname = rs.getString("cardname");
                cardNameArray.add(cardname);
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return cardNameArray;
    }

    /**
     * Deletes whole content from the deck of a user
     * @param username Username from which the deck should be deleted
     * @return True if success, else false
     */
    public boolean delDeck(String username) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("delete from user_deck where username = ?;");
            ps.setString(1, username);
            ps.executeUpdate();
            ps.close();
            c.close();
            System.out.println("User Deck: " + username + ", deleted");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Gets random package out of the shop
     * @param username -> user which the package wants
     * @return the package from the shop, null if error
     */
    public Package userAcquirePackage(String username) {
        String id = "";
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select \"ID\" as id from package LIMIT 1;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getString("id");
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        String packagename = "", cardname, elementtyp, cardtype;
        int damage;
        Cards cards = new Cards(new ArrayList<>());
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select  i as zeilennummer,  package.\"ID\" as id, package.name as packagename, c.name as cardname, c.DAMAGE as damage, c.ELEMENTTYP as elementtyp, c.CARDTYPE as cardtype from package join card c on c.name = package.name where \"ID\" = ?;");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getString("id");
                packagename = rs.getString("packagename");
                cardname = rs.getString("cardname");
                elementtyp = rs.getString("elementtyp");
                cardtype = rs.getString("cardtype");
                damage = rs.getInt("damage");
                Card newCard = new Card(cardname, elementtyp + cardtype, damage);
                cards.addCard(newCard);
                if(!addUserCard(username, newCard.getName())){
                    return null;
                }
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        if(!deletePackage(id)){
            return null;
        }
        if (cards.getCards().size() != 0) {
            return new Package(cards, packagename, 5);
        } else {
            return null;
        }
    }

    /**
     * Deletes a user card from the db
     * @param username card to delete from
     * @param cardname card from the user to delete
     * @return True if success, else false
     */
    public boolean delUserCard(String username, String cardname) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("delete from user_cards where username = ? and name = ?;");
            ps.setString(1, username);
            ps.setString(2, cardname);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Adds a card to user_cards
     * @param username Username the card gets
     * @param cardName Card the user gets
     * @return True if success, else false
     */
    public boolean addUserCard(String username, String cardName){
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("INSERT INTO user_cards (username, name, gesperrt) VALUES ( ?,?,?);");
            ps.setString(1, username);
            ps.setString(2, cardName);
            ps.setBoolean(3, false);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Deletes package by name
     * @param name Name of package which is deleted
     * @return True if success, else false
     */
    public boolean deletePackage(String name) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("DELETE FROM package WHERE \"ID\" = ?;");
            ps.setString(1, name);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Set coins
     * @param coins number to be set
     * @param username Username where the coins are updated
     * @return True if success, else false
     */
    public boolean updateCoins(int coins, String username) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("UPDATE users SET coins = ? WHERE username = ?;");
            ps.setInt(1, coins);
            ps.setString(2, username);
            ps.executeUpdate();
            System.out.println("Coins updated");
            ps.close();
            c.close();
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Give the amount of available coins back
     * @param username -> username from which the coins should get from
     * @return Amount of coins, -1 is error
     */
    public int checkCoins(String username) {
        int coins = 0;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("Select coins from users where username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                coins = rs.getInt("coins");
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return -1;
        }
        return coins;
    }

    /**
     * Checks by the name of the card, if card already exists
     * @param name Name to check
     * @return True if card already exists
     */
    public boolean cardExists(String name) {
        int count = 0;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select count(*) from card where name = ?;");
            ps.setString(1, name);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                count = rs.getInt("count");
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
        return count == 1;
    }

    /**
     * Puts a card into db
     * @param card which is added
     * @return true if success, false if error
     */
    public boolean addCard(Card card) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("insert into card (NAME, DAMAGE, ELEMENTTYP, CARDTYPE) values (?,?,?,?)");
            ps.setString(1, card.getName());
            ps.setDouble(2, card.getDamage());
            ps.setString(3, card.getElementTyp().name());
            ps.setString(4, card.getCardType().name());
            ps.executeUpdate();
            ps.close();
            c.close();
            System.out.println("Card added");
            return true;
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Determines next package id
     * @return the next Package id, if 0 -> error
     */
    public int nextPackageId() {
        String id = "";
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select max(\"ID\") from package;");
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                id = rs.getString("max");
            }
            if (id == null) {
                id = "0";
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return 0;
        }
        return Integer.parseInt(id) + 1;
    }

    /**
     * puts a package into the db, if a card of this package isn't already in db
     * @param pack - Pack which should be added
     * @return true if success, false if error
     */
    public boolean addPackage(Package pack) {
        try {
            for (Card ca : pack.getCards()) {
                if (!cardExists(ca.getName())) {
                    if (!addCard(ca)) {
                        return false;
                    }
                }
                Connection c = PostgresHelper.con();
                PreparedStatement ps = c.prepareStatement("INSERT INTO package (\"ID\", \"name\") values (?,?);");
                ps.setString(1, pack.getName());
                ps.setString(2, ca.getName());
                ps.executeUpdate();
                ps.close();
                c.close();
                System.out.println("Package added");
            }
        } catch (SQLException ignored) {
            System.out.println("Error add package");
            return false;
        }
        return true;
    }

    /**
     * puts a user into the db
     * @param username Username of User
     * @param password Passwort of User
     * @param nachname nachname of User
     * @param bio Bio of User
     * @param image Image of User
     * @return true if all successfull, false if not
     */
    public boolean addUser(String username, String password, String nachname, String bio, String image) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("INSERT INTO users (username, nachname, password, bio, image) values (?,?,?,?,?)");
            ps.setString(1, username);
            ps.setString(2, nachname);
            ps.setString(3, password);
            ps.setString(4, bio);
            ps.setString(5, image);
            ps.executeUpdate();
            ps.close();
            c.close();
            System.out.println("User added");
        } catch (SQLException ignored) {
            System.out.println("User add error");
            return false;
        }
        return true;
    }

    /**
     * Updates Nachnamen, Bio and Image of user with given username
     * @param username user to change
     * @param bio new BIO
     * @param image new Image
     * @param name new Nachname
     * @return true if success, else false
     */
    public boolean updateUser(String username, String bio, String image, String name) {
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("UPDATE users SET nachname = ?, bio = ?, image = ? WHERE username LIKE ? ESCAPE '#'");
            ps.setString(1, name);
            ps.setString(2, bio);
            ps.setString(3, image);
            ps.setString(4, username);
            ps.executeUpdate();
            ps.close();
            c.close();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * search the User with the Username
     * gives user object back
     * @param uname username to see
     * @return user as user object, null if error
     */
    public User getUser(String uname){
        String username = "", password = "", bio="", image="";
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("SELECT * FROM users where username = ?;");
            ps.setString(1, uname);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                username = rs.getString("username");
                password = rs.getString("password");
                bio = rs.getString("bio");
                image = rs.getString("image");
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return new User(new Credentials(username, password), username, username, new Coins(20), bio, image);
    }

    /**
     * gets card out of DB, with the given card id
     * @param id id of the Card which should be fetched
     * @return null if error
     */
    public Card getCardFromID(String id){
        Card toreturn = null;
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select * from card where NAME = ?;");
            ps.setString(1, id);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                int damage =rs.getInt("damage");
                String elementtyp =rs.getString("elementtyp");
                String cardtype=rs.getString("cardtype");
                toreturn = new Card(id, elementtyp+cardtype, damage);
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return toreturn;
    }

    /**
     * gets all cards of given card id list
     * @param cardnamearray Card IDs in the List
     * @return if null, it was an error
     */
    public Cards getCardsFromIDList(List<String> cardnamearray){
        Cards allCards = new Cards(new ArrayList<>());
        for (String st : cardnamearray) {
            try {
                Connection c = PostgresHelper.con();
                PreparedStatement ps = c.prepareStatement("select * from card where NAME = ?;");
                ps.setString(1, st);
                ResultSet rs = ps.executeQuery();
                while (rs.next()) {
                    int damage = rs.getInt("damage");
                    String elementtyp = rs.getString("elementtyp");
                    String cardtype = rs.getString("cardtype");
                    allCards.addCard(new Card(st, elementtyp+cardtype, damage));
                }
                rs.close();
                ps.close();
                c.close();
            } catch (Exception e) {
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                return null;
            }
        }
        return allCards;
    }

    /**
     * gets all cards of user
     * @param username Username of User
     * @return Cards object, if its null, is an error
     */
    public Cards getCards(String username){
        String cardname;
        ArrayList<String> cardnamenarray = new ArrayList<>();
        try {
            Connection c = PostgresHelper.con();
            PreparedStatement ps = c.prepareStatement("select * from user_cards where username = ?;");
            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                cardname = rs.getString("name");
                cardnamenarray.add(cardname);
            }
            rs.close();
            ps.close();
            c.close();
        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            return null;
        }
        return getCardsFromIDList(cardnamenarray);
    }
}
