package myServer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CardSuiteManager {
    // reference: https://www.youtube.com/watch?v=aCo-iNedw2g


    final static int CARD_NUMBER = 13 * 4;
    private final static int DISTRIBUTION_CARD_NUMBER_AT_BEGINNING = 4;

    // The cards in available pile, face-down, never exposed these cards to clients
    private ArrayList<Card> availableCards = null;
    // The cards drawn from available card pile, they may be in discarded card pile, clients' decks.
    private ArrayList<Card> playedCards = null;
    // The cards discarded by clients, face-up
    private ArrayList<Card> discardedCards = null;

    // The players participated in this game
    private final ArrayList<Player> players = new ArrayList<>();

    // The game is terminate or not. It mean at least one client reaches more than 100 score.
    private boolean terminated = false;

    // Logger handler
    private final static Logger logger = Logger.getLogger(CardSuiteManager.class.getName());

    public CardSuiteManager() {
        generateCards(true);
    }
    public CardSuiteManager(boolean shouldShuffle) {
        generateCards(shouldShuffle);
    }

    public void generateCards(boolean shouldShuffle) {
        if (this.terminated) {
            logger.log(Level.INFO, "The game is terminated, no need to generate cards anymore.");
            this.availableCards = null;
            this.playedCards = null;
            this.discardedCards = null;
            return;
        }
        this.availableCards = new ArrayList<>();
        this.playedCards = new ArrayList<>();
        this.discardedCards = new ArrayList<>();

        this.generateUnShuffledCards();
        this.shuffleCards(shouldShuffle);
        logger.log(Level.INFO, "The cards is generated successfully.");

    }
    /**
     * Generate cards
     */
    private void generateUnShuffledCards() {
        for (int i = 0; i <= 13; i ++) {
            if (i == 0 || i == 13) {
                this.availableCards.add(new Card(i, TypeDefs.SPADE));
                this.availableCards.add(new Card(i, TypeDefs.CLUB));
            } else if (i == 7 || i == 8) {
                // NOTE: 7 or 8: 'peek'
                this.availableCards.add(new Card(i, TypeDefs.SPADE, TypeDefs.PEEK));
                this.availableCards.add(new Card(i, TypeDefs.CLUB, TypeDefs.PEEK));
                this.availableCards.add(new Card(i, TypeDefs.HEART, TypeDefs.PEEK));
                this.availableCards.add(new Card(i, TypeDefs.DIAMOND, TypeDefs.PEEK));
            } else if (i == 9 || i == 10) {
                // NOTE: 9 or 10: 'spy'
                this.availableCards.add(new Card(i, TypeDefs.SPADE, TypeDefs.SPY));
                this.availableCards.add(new Card(i, TypeDefs.CLUB, TypeDefs.SPY));
                this.availableCards.add(new Card(i, TypeDefs.HEART, TypeDefs.SPY));
                this.availableCards.add(new Card(i, TypeDefs.DIAMOND, TypeDefs.SPY));
            } else if (i == 11 || i == 12) {
                // 11 or 12: 'swap'
                this.availableCards.add(new Card(i, TypeDefs.SPADE, TypeDefs.SWAP));
                this.availableCards.add(new Card(i, TypeDefs.CLUB, TypeDefs.SWAP));
                this.availableCards.add(new Card(i, TypeDefs.HEART, TypeDefs.SWAP));
                this.availableCards.add(new Card(i, TypeDefs.DIAMOND, TypeDefs.SWAP));
            } else {
                this.availableCards.add(new Card(i, TypeDefs.SPADE));
                this.availableCards.add(new Card(i, TypeDefs.CLUB));
                this.availableCards.add(new Card(i, TypeDefs.HEART));
                this.availableCards.add(new Card(i, TypeDefs.DIAMOND));
            }
        }
        logger.log(Level.FINER, "Generate plain cards successfully.");
    }

    /**
     * Shuffle cards
     * @param shouldShuffle the flag to show whether the cards should be shuffled
     */
    private void shuffleCards(boolean shouldShuffle) {
        if (shouldShuffle) {
            logger.log(Level.FINER, "Shuffling the cards");
            Collections.shuffle(this.availableCards);
        } else {
            logger.log(Level.FINER, "We don't want to shuffle the cards");
        }
    }

    /**
     * Terminate the game
     */
    public void terminate() {
        this.terminated = true;
    }

    public void addPlayer(Player player) {
        this.players.add(player);
    }
    public ArrayList<Player> getPlayers() {
        return this.players;
    }

    /**
     * @TODO Make this function easy to extend
     * Calculate the scores once caboed
     */
    public void calcScores() {
        // ArrayList<Player> allPlayers = this.cardSuiteManager.getPlayers();
        // Case1: Checking the special case, (0, 0, 13, 13)
        for (int i = 0; i < players.size(); i ++) {
            Player player = players.get(i);
            if (player.getCards().size() == 4) {
                Card ca = player.getCards().get(0);
                Card cb = player.getCards().get(1);
                Card cc = player.getCards().get(2);
                Card cd = player.getCards().get(3);

                boolean special = false;
                if (ca.getValue() == cb.getValue() && (ca.getValue() == 0 || ca.getValue() == 13)) {
                    if (cc.getValue() == cd.getValue() && (cd.getValue() == 0 || cd.getValue() == 13)) {
                        // Player has special cards (0, 0, 13, 13) or (13, 13, 0, 0)
                        special = true;
                    }
                } else if (ca.getValue() == cc.getValue() && (ca.getValue() == 0 || ca.getValue() == 13)) {
                    if (cb.getValue() == cd.getValue() && (cd.getValue() == 0 || cd.getValue() == 13)) {
                        // Player has special cards (0, 13, 0, 13) or (13, 0, 13, 0)
                        special = true;
                    }
                } else if (ca.getValue() == cd.getValue() && (ca.getValue() == 0 || ca.getValue() == 13)) {
                    if (cb.getValue() == cc.getValue() && (cb.getValue() == 0 || cb.getValue() == 13)) {
                        // Player has special cards (0, 13, 13, 0) or (13, 0, 0, 13)
                        special = true;
                    }
                }
                if (special) {
                    for (int k = 0; k < players.size(); k++) {
                        Player _player = players.get(k);
                        if (k != i) {
                            _player.setScore(_player.getScore() + 50);
                            if (_player.getScore() == 100 || _player.getScore() == 50) {
                                _player.setScore(_player.getScore() / 2);
                             }
                            if (_player.getScore() > 100) {
                                 this.terminate();
                             }
                        }
                    }
                    // Inside speical case
                    return;
                }
            }
        }

        // Case 2: common case
        int smallestPoint = Integer.MAX_VALUE;
        for (int i = 0; i < players.size(); i ++) {
            if (smallestPoint > players.get(i).calculatePoints()) {
                smallestPoint = players.get(i).calculatePoints();
            }
        }
        for (int i = 0; i < players.size(); i ++) {
            Player player = this.players.get(i);
            if (player.getCalledCabo()) {
                if (smallestPoint != player.calculatePoints()) {
                    player.setScore(player.calculatePoints() * 2 + player.getScore());
                } else {
                    // get zero score this time
                }
            } else {
                player.setScore(player.getScore() + player.calculatePoints());

            }
            if (player.getScore() == 100 || player.getScore() == 50) {
                player.setScore(player.getScore()/2);
            }
            if (player.getScore() > 100) {
                this.terminate();
            }
        }
    }
    /**
     * @Debug: Simply check the implementation
     */
    public void debug() {
        System.out.println("============== DEBUG ====================");
        System.out.println("----- Available Card ---------");
        for (int i = 0; i < this.availableCards.size(); i ++) {
            System.out.println(this.availableCards.get(i));
        }
        System.out.println("----- Played Card ---------");
        for (int i = 0; i < this.playedCards.size(); i ++) {
            System.out.println(this.playedCards.get(i));
        }
        System.out.println("----- Discarded Card ---------");
        for (int i = 0; i < this.discardedCards.size(); i ++) {
            System.out.println(this.discardedCards.get(i));
        }
        if (this.playedCards.size() + this.availableCards.size() != CARD_NUMBER) {
            System.out.println("Error");
        } else {
            System.out.println("Seems Correct");
        }
        System.out.println("=====================================");

    }

    /**
     * Draw a card from `availableCards` pile
     * @return a card retrieve from `availableCard`
     */
    public Card takeFirstCardFromAvailableCards() {
        Card card = this.availableCards.remove(0);
        this.playedCards.add(card);
        return card;
    }

    public ArrayList<Card> getAvailableCards() {
        return this.availableCards;
    }

    public ArrayList<Card> getDiscardedCards() {
        return this.discardedCards;
    }
    public ArrayList<Card> getPlayedCards() {
        return this.playedCards;
    }

    /**
     * Distribute cards to all participated players;
     */
    public void distributeCardsAtBeginning() {
        players.forEach(player -> {
            player.reset();
            for (int i = 0; i < DISTRIBUTION_CARD_NUMBER_AT_BEGINNING; i ++) {
                player.drawCard();
            }
        });
    }

}
