package at.hassmann.objects;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class Battle {
    private final User player1;
    private User player2;
    private int scorePlayer1;
    private int scorePlayer2;
    private final int id;
    private Cards deckPlayer1;
    private Cards deckPlayer2;
    private final Cards deckPlayer1Init;
    private Cards deckPlayer2Init;
    private final ArrayList<String> log = new ArrayList<>();
    private ArrayList<Card> d1 = new ArrayList<>(), d2 = new ArrayList<>();

    /**
     * creates new Battle
     * @param id Id of the battle
     * @param player1 Player 1 of the Battle
     * @param deckPlayer1 Deck from Player 1 as json
     */
    public Battle(int id, User player1, Cards deckPlayer1){
        this.id = id;
        this.player1 = player1;
        this.deckPlayer1 = deckPlayer1;
        this.scorePlayer1 = 100;
        this.scorePlayer2 = 100;
        this.deckPlayer2 = null;
        this.deckPlayer1Init = deckPlayer1;
        this.deckPlayer2Init = null;
    }

    /**
     * starts a fight with both players
     * @return true when success, else false
     */
    public boolean doFight(){
        System.out.println(player1.getName() + " |vs| " + player2.getName());
        if(deckPlayer1.getCards().size() == 4 && deckPlayer2.getCards().size() == 4) {
            this.d1 = deckPlayer1.getCards();
            this.d2 = deckPlayer2.getCards();
            Collections.shuffle(this.d1);     //SHUFFLE DECK
            Collections.shuffle(this.d2);
            deckPlayer1.setCards(this.d1);
            deckPlayer1.setCards(this.d2);
            //Rounds
            int maxroundcount = 100, counter = 0, counter2 = 0;
            while(counter<maxroundcount) {
                counter++;
                counter2++;
                if(counter2 < this.d1.size() || counter2 < this.d2.size()){
                    counter2 = 0;
                }
                if (this.d1.size() > 0 && this.d2.size() > 0 && counter<=maxroundcount) {
                    System.out.println("Runde: " + counter);
                    System.out.println("Deck 1 size: " + this.d1.size() + " Deck 2 size: " + this.d2.size() + " counter2: " + counter2);
                    Card c1 = this.d1.get(counter2);
                    Card c2 = this.d2.get(counter2);
                    System.out.println("Card 1: " + c1.getElementTyp() + c1.getCardType() + "\nCard 2: " + c2.getElementTyp() + c2.getCardType());
                    //Same monster typ
                    if (!c1.getCardType().name().equalsIgnoreCase("SPELL") && !c2.getCardType().name().equalsIgnoreCase("SPELL")) {
                        //check, if 1 card is a dragon
                        if ((c1.getCardType().name().equalsIgnoreCase("DRAGON") && !c2.getCardType().name().equalsIgnoreCase("DRAGON") || (!c1.getCardType().name().equalsIgnoreCase("DRAGON") && c2.getCardType().name().equalsIgnoreCase("DRAGON")))) {
                            //1 card is a dragon
                            Card dragon;
                            int dragonOwner;
                            if (c1.getCardType().name().equalsIgnoreCase("DRAGON")) {
                                dragon = c1;
                                dragonOwner = 1;
                            } else if (c2.getCardType().name().equalsIgnoreCase("DRAGON")) {
                                dragon = c2;
                                dragonOwner = 2;
                            } else {
                                return false;
                            }
                            if ((c1.getCardType().name().equalsIgnoreCase("GOBLIN") && !c2.getCardType().name().equalsIgnoreCase("GOBLIN") || (!c1.getCardType().name().equalsIgnoreCase("GOBLIN") && c2.getCardType().name().equalsIgnoreCase("GOBLIN")))) {
                                //1 card is goblin
                                Card goblin;
                                if (c1.getCardType().name().equalsIgnoreCase("GOBLIN")) {
                                    goblin = c1;
                                } else if (c2.getCardType().name().equalsIgnoreCase("GOBLIN")) {
                                    goblin = c2;
                                } else {
                                    return false;
                                }
                                if (dragon.getDamage() > goblin.getDamage()) {
                                    //Dragon is stronger
                                    if (dragonOwner == 1) {
                                        this.d1.add(c2);
                                        this.d2.remove(c2);
                                        this.scorePlayer1 += 3;
                                        this.scorePlayer2 -= 5;
                                    } else {
                                        this.d2.add(c1);
                                        this.d1.remove(c1);
                                        this.scorePlayer1 -= 5;
                                        this.scorePlayer2 += 3;
                                    }
                                    this.log.add("Player " + dragonOwner + " gewinnt!\nDrache ist stärker! Drache: " + dragon.getDamage() + " vs Goblin: " + goblin.getDamage() + "\nPlayer 1 score: " + scorePlayer1 + "\nPlayer 2 score: " + scorePlayer2);
                                }
                            } else if ((c1.getCardType().name().equalsIgnoreCase("ELF") && c1.getElementTyp().name().equalsIgnoreCase("FIRE") && !c2.getCardType().name().equalsIgnoreCase("ELF") || (!c1.getCardType().name().equalsIgnoreCase("ELF") && c2.getElementTyp().name().equalsIgnoreCase("FIRE") && c2.getCardType().name().equalsIgnoreCase("ELF")))) {
                                //1 card fireelf other card dragon
                                Card fireelf;
                                if (dragonOwner == 1) {
                                    fireelf = c2;
                                } else {
                                    fireelf = c1;
                                }

                                if (fireelf.getDamage() > dragon.getDamage()) {
                                    //fireelf is stronger
                                    if (dragonOwner == 1) {
                                        this.d2.add(c1);
                                        this.d1.remove(c1);
                                        this.scorePlayer2 += 3;
                                        this.scorePlayer1 -= 5;
                                    } else {
                                        this.d1.add(c2);
                                        this.d2.remove(c2);
                                        this.scorePlayer2 -= 5;
                                        this.scorePlayer1 += 3;
                                    }
                                    this.log.add("Player " + dragonOwner + " gewinnt!\nWizzard ist stärker! Drache: " + dragon.getDamage() + " vs FireElves: " + fireelf.getDamage() + "\nPlayer 1 score: " + scorePlayer1 + "\nPlayer 2 score: " + scorePlayer2);
                                }
                                //nothing else to do, because dragen can't attack elf
                            } else {
                                calculateWinnerMoveCards(c1, c2);
                            }

                            //checks if 1 card is wizzard and other card is ork
                        } else if ((c1.getCardType().name().equalsIgnoreCase("WIZZARD") && c2.getCardType().name().equalsIgnoreCase("ORK") || (c2.getCardType().name().equalsIgnoreCase("WIZZARD") && c1.getCardType().name().equalsIgnoreCase("ORK")))) {
                            Card wizzard = null;
                            Card ork = null;
                            int wizzardOwner = 0;
                            if (c1.getCardType().name().equalsIgnoreCase("WIZZARD")) {
                                wizzardOwner = 1;
                                wizzard = c1;
                                ork = c2;
                            } else if (c2.getCardType().name().equalsIgnoreCase("WIZZARD")) {
                                wizzardOwner = 2;
                                wizzard = c2;
                                ork = c1;
                            }
                            if (wizzard != null && wizzard.getDamage() > ork.getDamage()) {
                                if (wizzardOwner == 1) {
                                    this.d1.add(c2);
                                    this.d2.remove(c2);
                                    this.scorePlayer1 += 3;
                                    this.scorePlayer2 -= 5;
                                } else {
                                    this.d2.add(c1);
                                    this.d1.remove(c1);
                                    this.scorePlayer2 += 3;
                                    this.scorePlayer1 -= 5;
                                }

                                this.log.add("Player " + wizzardOwner + " gewinnt!\nWizzard ist stärker! Wizzard: " + wizzard.getDamage() + " vs Ork: " + ork.getDamage() + "\nPlayer 1 score: " + scorePlayer1 + "\nPlayer 2 score: " + scorePlayer2);
                            }
                            //nothing else to do, because ork can't harm wizzard
                        } else {
                            //PURE MONSTER
                            calculateWinnerMoveCards(c1, c2);
                        }
                    } else {//PURE SPELL and mixed
                        double damagePlayer1, damagePlayer2;

                        if (c1.getCardType().name().equalsIgnoreCase("KNIGHT") || c2.getCardType().name().equalsIgnoreCase("KNIGHT")) {
                            //Mixed with "special effekt" KNIGHT
                            Card knight = null, other = null;
                            int knightOwner = 0;
                            if (c1.getCardType().name().equalsIgnoreCase("KNIGHT")) {
                                knight = c1;
                                other = c2;
                                knightOwner = 1;
                            } else if (c2.getCardType().name().equalsIgnoreCase("KNIGHT")) {
                                knight = c2;
                                other = c1;
                                knightOwner = 2;
                            }
                            double damageKnight = -1, damageOther = -1;
                            if (Objects.requireNonNull(other).getElementTyp().name().equalsIgnoreCase("WATER")) {
                                //dead
                                damageKnight = 0;
                                damageOther = other.getDamage();
                            } else if (other.getElementTyp().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getElementTyp().name().equals("REGULAR")) {
                                //not effective
                                damageKnight = knight.getDamage() / 2;
                                //effective
                                damageOther = other.getDamage() * 2;
                            } else if (other.getElementTyp().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getElementTyp().name().equals("FIRE")) {
                                //no effect
                                damageKnight = knight.getDamage();
                                //no effect
                                damageOther = other.getDamage();
                            } else if (other.getElementTyp().name().equalsIgnoreCase("FIRE") && Objects.requireNonNull(knight).getElementTyp().name().equals("WATER")) {
                                //effective
                                damageKnight = knight.getDamage() * 2;
                                //not effective
                                damageOther = other.getDamage() / 2;
                            } else if (other.getElementTyp().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getElementTyp().name().equals("REGULAR")) {
                                //no effect
                                damageKnight = knight.getDamage();
                                //no effect
                                damageOther = other.getDamage();
                            } else if (other.getElementTyp().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getElementTyp().name().equals("FIRE")) {
                                //effective
                                damageKnight = knight.getDamage() * 2;
                                //not effective
                                damageOther = other.getDamage() / 2;
                            } else if (other.getElementTyp().name().equalsIgnoreCase("REGULAR") && Objects.requireNonNull(knight).getElementTyp().name().equals("WATER")) {
                                //not effective
                                damageKnight = knight.getDamage() / 2;
                                //effective
                                damageOther = other.getDamage() * 2;
                            }
                            if (damageKnight > damageOther) {
                                if (knightOwner == 1) {
                                    p1win(c1,c2);
                                } else {
                                    p2win(c1, c2);
                                }
                            } else if (damageKnight < damageOther) {
                                if (knightOwner == 2) {
                                    p1win(c1, c2);
                                } else {
                                    p2win(c1, c2);
                                }
                            }
                        } else if (c1.getCardType().name().equalsIgnoreCase("KRAKEN") || c2.getCardType().name().equalsIgnoreCase("KRAKEN")) {
                            //Mixed with "special effekt" KRAKEN
                            if (c1.getCardType().name().equalsIgnoreCase("KRAKEN")) {
                                p1win(c1, c2);
                            } else if (c2.getCardType().name().equalsIgnoreCase("KRAKEN")) {
                                p2win(c1, c2);
                            }
                        } else {
                            ////PURE SPELL and mixed with elements
                            //Player 1 calculate damage
                            damagePlayer1 = calculateEffectiveness(c1, c2);
                            //P2 damage
                            damagePlayer2 = calculateEffectiveness(c2, c1);
                            if (damagePlayer1 > -1 && damagePlayer2 > -1) {
                                if (damagePlayer1 > damagePlayer2) {
                                    p1win(c1, c2);
                                } else if (damagePlayer2 > damagePlayer1) {
                                    p2win(c1, c2);
                                }
                            } else {
                                return false;
                            }
                        }
                    }
                }else {
                    return true;
                }
            }
            this.deckPlayer1 = new Cards(this.d1);
            this.deckPlayer2 = new Cards(this.d2);
        }else{
            System.err.println("Einer der Spieler hat zu wenige Karten im Deck");
            return false;
        }
        return true;
    }

    /**
     * if player1 wins
     * @param c1 Card player 1
     * @param c2 Card player 2
     */
    private void p1win(Card c1, Card c2){
        this.d1.add(c2);
        this.d2.remove(c2);
        this.scorePlayer1 += 3;
        this.scorePlayer2 -= 5;
        this.log.add("Player 1 gewinnt!\n" + c1.getElementTyp() + c1.getCardType() + " ist stärker! " + c1.getElementTyp() + c1.getCardType() + ": " + c1.getDamage() + " vs " + c2.getElementTyp() + c2.getCardType() + ": " + c2.getDamage() + "\nPlayer 1 score: " + scorePlayer1 + "\nPlayer 2 score: " + scorePlayer2);
    }

    /**
     * if player1 wins
     * @param c1 Card player 1
     * @param c2 Card player 2
     */
    private void p2win(Card c1, Card c2){
        this.d2.add(c1);
        this.d1.remove(c1);
        this.scorePlayer2 += 3;
        this.scorePlayer1 -= 5;
        this.log.add("Player 2 gewinnt!\n" + c2.getElementTyp() + c2.getCardType() + " ist stärker! " + c2.getElementTyp() + c2.getCardType() + ": " + c1.getDamage() + " vs " + c1.getElementTyp() + c1.getCardType() + ": " + c2.getDamage() + "\nPlayer 1 score: " + scorePlayer1 + "\nPlayer 2 score: " + scorePlayer2);
    }

    /**
     * determine winner this round, moved decks and set score
     * @param c1 Card p1
     * @param c2 Card p2
     */
    private void calculateWinnerMoveCards(Card c1, Card c2) {
        if (c1.getDamage() > c2.getDamage()) {
            p1win(c1, c2);
        } else if (c1.getDamage() < c2.getDamage()) {
            p2win(c1, c2);
        }
    }

    /**
     * calculate damage of cards
     * @param c1 Card von p1
     * @param c2 Card von p2
     * @return Damage
     */
    private double calculateEffectiveness(Card c1, Card c2){
        double damagePlayer1 = 0;
        switch (c1.getElementTyp().name().toUpperCase()) {
            case "FIRE":
                switch (c2.getElementTyp().name().toUpperCase()) {
                    case "REGULAR":
                        //effective
                        damagePlayer1 = c1.getDamage() * 2;
                        break;
                    case "WATER":
                        //not effective
                        damagePlayer1 = c1.getDamage() / 2;
                        break;
                    case "FIRE":
                        //no effect
                        damagePlayer1 = c1.getDamage();
                        break;
                }
                break;
            case "WATER":
                switch (c2.getElementTyp().name().toUpperCase()) {
                    case "FIRE":
                        //effective
                        damagePlayer1 = c1.getDamage() * 2;
                        break;
                    case "WATER":
                        //no effect
                        damagePlayer1 = c1.getDamage();
                        break;
                    case "REGULAR":
                        //not effective
                        damagePlayer1 = c1.getDamage() / 2;
                        break;
                }
                break;
            case "REGULAR":
                switch (c2.getElementTyp().name().toUpperCase()) {
                    case "WATER":
                        //effective
                        damagePlayer1 = c1.getDamage() * 2;
                        break;
                    case "FIRE":
                        //not effective
                        damagePlayer1 = c1.getDamage() / 2;
                        break;
                    case "REGULAR":
                        //no effect
                        damagePlayer1 = c1.getDamage();
                        break;
                }
                break;
        }
        return damagePlayer1;
    }

    /**
     * Get the log
     * @return log as ArrayList
     */
    public ArrayList<String> getLog() {
        return this.log;
    }


    /**
     * Get Battle id
     * @return battleID
     */
    public int getId() {
        return id;
    }

    /**
     * Get Player 1 as User
     * @return User Obj
     */
    public User getPlayer1() {
        return player1;
    }

    /**
     * Get Player 2 as User
     * @return User Obj
     */
    public User getPlayer2() {
        return player2;
    }

    /**
     * Set Player 1 as User
     * @param player2  User Obj
     */
    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    /**
     * Get Score Player 1
     * @return Score of player
     */
    public int getScorePlayer1() {
        return scorePlayer1;
    }

    /**
     * Get Score Player 2
     * @return Score of player
     */
    public int getScorePlayer2() {
        return scorePlayer2;
    }

    /**
     * Get Deck Player 1
     * @return Deck as Cards of player
     */
    public Cards getDeckPlayer1() {
        return deckPlayer1;
    }

    /**
     * Get Deck Player 2
     * @return Deck as Cards of player
     */
    public Cards getDeckPlayer2() {
        return deckPlayer2;
    }

    /**
     * Set Deck Player 1
     * @param deckPlayer2  Deck as Cards of player
     */
    public void setDeckPlayer2(Cards deckPlayer2) {
        this.deckPlayer2 = deckPlayer2;
        this.deckPlayer2Init = deckPlayer2;
    }

    /**
     * Get start deck player 1
     * @return Deck at begin of battle
     */
    public Cards getDeckPlayer1Init() {
        return deckPlayer1Init;
    }

    /**
     * Get start deck player 1 2
     * @return Deck at begin of battle
     */
    public Cards getDeckPlayer2Init() {
        return deckPlayer2Init;
    }
}
