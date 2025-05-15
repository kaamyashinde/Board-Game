package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import lombok.Getter;

/**
 * Controller class specifically for Ludo game logic.
 */
public class LudoController extends BaseGameController {
    private static final Logger LOGGER = Logger.getLogger(LudoController.class.getName());
    
    // Ludo specific data
    private final Map<String, List<Integer>> playerTokenPositions = new HashMap<>();
    private final Map<String, List<Boolean>> playerTokenHome = new HashMap<>();
    private final Map<String, List<Boolean>> playerTokenFinished = new HashMap<>();
    private final int[] homePositions = {0, 13, 26, 39}; // Starting positions for each player
    private final int[] finishPositions = {50, 51, 52, 53}; // Finish positions for each player
    
    @Getter
    private boolean movingPiece = false;

    public LudoController(BoardGame boardGame) {
        super(boardGame);
        LOGGER.info("LudoController initialized");
    }

    @Override
    public void setPlayerNames(List<String> playerNames) {
        super.setPlayerNames(playerNames);
        // Initialize token positions for all players
        for (String playerName : playerNames) {
            List<Integer> positions = new ArrayList<>();
            List<Boolean> home = new ArrayList<>();
            List<Boolean> finished = new ArrayList<>();
            
            // Initialize 4 tokens for each player
            for (int i = 0; i < 4; i++) {
                positions.add(-1); // -1 indicates token is in home
                home.add(true);
                finished.add(false);
            }
            
            playerTokenPositions.put(playerName, positions);
            playerTokenHome.put(playerName, home);
            playerTokenFinished.put(playerName, finished);
        }
    }

    @Override
    public void handlePlayerMove() {
        if (!diceRolled) {
            LOGGER.warning("Dice must be rolled before moving");
            return;
        }

        String currentPlayer = getCurrentPlayerName();
        int[] diceValues = boardGame.getCurrentDiceValues();
        int steps = diceValues[0]; // Ludo uses only one die

        // Check if player can move any token
        boolean canMove = false;
        List<Integer> positions = playerTokenPositions.get(currentPlayer);
        List<Boolean> home = playerTokenHome.get(currentPlayer);
        List<Boolean> finished = playerTokenFinished.get(currentPlayer);

        for (int i = 0; i < positions.size(); i++) {
            if (!home.get(i) && !finished.get(i)) {
                canMove = true;
                break;
            }
        }

        if (!canMove && steps != 6) {
            LOGGER.info(currentPlayer + " has no legal moves. Passing turn.");
            nextPlayer();
            return;
        }

        // If player rolled a 6, they can move a token from home
        if (steps == 6) {
            for (int i = 0; i < home.size(); i++) {
                if (home.get(i)) {
                    home.set(i, false);
                    positions.set(i, homePositions[playerNames.indexOf(currentPlayer)]);
                    LOGGER.info(currentPlayer + " moved token " + (i + 1) + " out of home");
                    break;
                }
            }
        }

        // Move tokens that are on the board
        for (int i = 0; i < positions.size(); i++) {
            if (!home.get(i) && !finished.get(i)) {
                int currentPos = positions.get(i);
                int newPos = (currentPos + steps) % 52; // 52 is the total number of positions on the board
                
                // Check if token has completed a lap
                if (newPos < currentPos) {
                    // Token has completed a lap, check if it can enter the finish area
                    int playerIndex = playerNames.indexOf(currentPlayer);
                    int finishPos = finishPositions[playerIndex];
                    if (newPos >= finishPos && newPos < finishPos + 6) {
                        finished.set(i, true);
                        LOGGER.info(currentPlayer + "'s token " + (i + 1) + " has finished!");
                    }
                }
                
                positions.set(i, newPos);
                LOGGER.info(currentPlayer + " moved token " + (i + 1) + " to position " + newPos);
            }
        }

        // Check if player has won
        boolean allFinished = true;
        for (boolean tokenFinished : finished) {
            if (!tokenFinished) {
                allFinished = false;
                break;
            }
        }

        if (allFinished) {
            LOGGER.info(currentPlayer + " has won the game!");
        } else {
            nextPlayer();
        }
    }

    /**
     * Gets the positions of all tokens for a player
     */
    public List<Integer> getPlayerTokenPositions(String playerName) {
        return playerTokenPositions.get(playerName);
    }

    /**
     * Gets whether a player's tokens are in home
     */
    public List<Boolean> getPlayerTokenHome(String playerName) {
        return playerTokenHome.get(playerName);
    }

    /**
     * Gets whether a player's tokens have finished
     */
    public List<Boolean> getPlayerTokenFinished(String playerName) {
        return playerTokenFinished.get(playerName);
    }

    /**
     * Rolls the dice for the current player
     */
    public void rollDiceForLudo() {
        boardGame.getDice().rollAllDice();
        diceRolled = true;
        LOGGER.info("Dice rolled: " + boardGame.getCurrentDiceValues()[0]);
    }

    /**
     * Updates a token's position
     */
    public void updateLudoTokenPosition(String playerName, int tokenIndex, int position) {
        List<Integer> positions = playerTokenPositions.get(playerName);
        positions.set(tokenIndex, position);
        LOGGER.info(playerName + "'s token " + (tokenIndex + 1) + " moved to position " + position);
    }

    /**
     * Moves a token out of home
     */
    public void moveTokenOutOfHome(String playerName, int tokenIndex) {
        List<Boolean> home = playerTokenHome.get(playerName);
        List<Integer> positions = playerTokenPositions.get(playerName);
        home.set(tokenIndex, false);
        positions.set(tokenIndex, homePositions[playerNames.indexOf(playerName)]);
        LOGGER.info(playerName + "'s token " + (tokenIndex + 1) + " moved out of home");
    }

    /**
     * Marks a token as finished
     */
    public void markTokenAsFinished(String playerName, int tokenIndex) {
        List<Boolean> finished = playerTokenFinished.get(playerName);
        finished.set(tokenIndex, true);
        LOGGER.info(playerName + "'s token " + (tokenIndex + 1) + " has finished!");
    }

    /**
     * Moves to the next player
     */
    public void nextLudoPlayer() {
        nextPlayer();
        LOGGER.info("Next player: " + getCurrentPlayerName());
    }
} 