# Monster Trading Card Game
***
## SWEN1 Project 
### by Thomas Hassmann
***
###Database tables:
*
    * Battle-Table
        * *Battle Einladungen*

    * Battle_log-Table
        * *Nach beendetem Battle wird hier der Log gespeichert*
      
    * Card-Table
        * *Alle Cards die in dem Spiel verwendet werden*
        * *Typ der Card wird automatisch in Card und Element Typ getrennt*

    * Package-Table
        * *Alle Packages, die die User bekommen können*

    * Trading-Table
        * *Alle aktiven Trading deals*
      
    * User_cards-Table
        * *Alle Cards der User*
      
    * User_deck-Table
        * *Deck von allen Usern*
        
    * User-Table
        * *Alle User die in MTCG registriert sind*
***        
####To empty DB: curl -X DELETE http://localhost:10001/db/del/all --header "Authorization: Basic kienboec-mtcgToken"
***
#### Cards 
* Card Type (z.B. Goblin) wird zu CardTyp und ElementTyp aufgespalten
* Alle Cards sind in der Cards Tabelle gespeichert
* Wenn der User die Cards acquired, dann wird das passende Package gelöscht und die Cards in seine user_card Table geschrieben
#### Deck
* Die Deckkarten werden in der Tabelle User_deck gespeichert
* Wird eine Card getradet, die im Deck ist, so wird das Deck des Users zurückgesetzt
* Wird ein Battle begonnen, dann muss der User 4 Cards im Deck haben.
#### Battle
* Beginnt ein User ein Battle und ist der erste, so wird in der Battle tabelle eine "Einladung" erstellt. 
  Die Einladung besteht aus: **Username, Userdeck, Battleid**
* Beginnt ein 2. User ein Battle, so sieht er das in der Battle Tabelle ein Spiel ohne 2. spieler ist, und kann beitreten.
#### Score & Stats    
* Score zeigt das Ergebnis des letzten Spiels an
* Stats zeigt die Ergebnisse der vergangenen Spiele
## API Beschreibung
#### GET
| ROUTE              | BESCHREIBUNG                       | ATTRIBUTE | RETURN                  | HEADER                                               |
|--------------------|------------------------------------|-----------|-------------------------|------------------------------------------------------|
| /cards             | show all acquired cards            | /         | Cards Object            | --header   "Authorization: Basic kienboec-mtcgToken" |
| /deck              | show deck                          | /         | Cards Object            | --header   "Authorization: Basic kienboec-mtcgToken" |
| /deck?format=plain | show deck different representation | /         | Card names Array        | --header   "Authorization: Basic kienboec-mtcgToken" |
| /users/{username}  | get user                           | /         | User Object             | --header   "Authorization: Basic kienboec-mtcgToken" |
| /stats             | get stats about all battles        | /         | All battle Logs         | --header   "Authorization: Basic kienboec-mtcgToken" |
| /score             | get score, about last battle       | /         | Last Battle Log         | --header   "Authorization: Basic kienboec-mtcgToken" |
| /tradings          | get trading deals                  | /         | List TradingDeal Object | --header   "Authorization: Basic kienboec-mtcgToken" |
#### POST
| ROUTE                  | BESCHREIBUNG              | ATTRIBUTE                            | RETURN              | HEADER                                               |
|------------------------|---------------------------|--------------------------------------|---------------------|------------------------------------------------------|
| /users                 | create   user             | Username, Password                   | User Object         | /                                                    |
| /sessions              | login user                | Username, Password                   | login true/false    | /                                                    |
| /packages              | create package            | [ID, Name, Damage]x5                 | Package Object      | --header   "Authorization: Basic admin-mtcgToken"    |
| /transactions/packages | acquire packages kienboec | /                                    | Package Object      | --header   "Authorization: Basic kienboec-mtcgToken" |
| /battles               | Battle                    | /                                    |  Anweisungen String | --header   "Authorization: Basic kienboec-mtcgToken" |
| /tradings              | create trading deal       | Id, CardToTrade, Type, MinimumDamage | TradingDeal Object  | --header   "Authorization: Basic kienboec-mtcgToken" |
| /tradings/{TradeID}    | Do Trade                  | CardID                               | New Card Object     | --header   "Authorization: Basic kienboec-mtcgToken" |
#### PUT
| ROUTE             | BESCHREIBUNG   | ATTRIBUTE        | RETURN       | HEADER                                               |
|-------------------|----------------|------------------|--------------|------------------------------------------------------|
| /deck             | configure deck | [ID, ID, ID, ID] | Cards Object | --header "Authorization:   Basic kienboec-mtcgToken" |
| /users/{username} | /              | Name, Bio, Image | User Object  | --header "Authorization: Basic kienboec-mtcgToken"   |
#### DELETE
| ROUTE               | BESCHREIBUNG        | ATTRIBUTE | RETURN | HEADER                                               |
|---------------------|---------------------|-----------|--------|------------------------------------------------------|
| /tradings/{TradeID} | delete trading deal | /         | /      | --header "Authorization:   Basic kienboec-mtcgToken" |
| /db/all             | Reset DB + Recreate | /         | /      |--header   "Authorization: Basic admin-mtcgToken"     |
