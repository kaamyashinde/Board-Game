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

    public static class MoveResult {
        public final int tokenIndex;
        public final int start;
        public final int end;
        public final String type; // "normal", "home", "finish", "capture"
        public MoveResult(int tokenIndex, int start, int end, String type) {
            this.tokenIndex = tokenIndex;
            this.start = start;
            this.end = end;
            this.type = type;
        }
    }

    /**
     * Rolls the dice and returns the value
     */
    public int rollDice() {
        boardGame.getDice().rollAllDice();
        diceRolled = true;
        int[] values = boardGame.getCurrentDiceValues();
        return (values != null && values.length > 0) ? values[0] : 0;
    }

    /**
     * Returns a list of legal token indices for the current player and dice value
     */
    public List<Integer> getLegalMoves(String playerName, int diceValue) {
        List<Integer> legal = new ArrayList<>();
        List<Integer> positions = playerTokenPositions.get(playerName);
        List<Boolean> home = playerTokenHome.get(playerName);
        List<Boolean> finished = playerTokenFinished.get(playerName);
        for (int i = 0; i < positions.size(); i++) {
            if (finished.get(i)) continue;
            if (home.get(i) && diceValue == 6) legal.add(i);
            if (!home.get(i) && !finished.get(i)) legal.add(i);
        }
        return legal;
    }

    /**
     * Moves a token for the player and returns the result
     */
    public MoveResult moveToken(String playerName, int tokenIndex, int diceValue) {
        List<Integer> positions = playerTokenPositions.get(playerName);
        List<Boolean> home = playerTokenHome.get(playerName);
        List<Boolean> finished = playerTokenFinished.get(playerName);
        int start = positions.get(tokenIndex);
        int end = start;
        String type = "normal";
        if (home.get(tokenIndex) && diceValue == 6) {
            // Move out of home
            home.set(tokenIndex, false);
            end = homePositions[playerNames.indexOf(playerName)];
            positions.set(tokenIndex, end);
            type = "home";
        } else if (!home.get(tokenIndex) && !finished.get(tokenIndex)) {
            // Move on board
            end = (start + diceValue) % 52;
            // Check for finish
            int playerIdx = playerNames.indexOf(playerName);
            int finishPos = finishPositions[playerIdx];
            if (start < finishPos && end >= finishPos) {
                finished.set(tokenIndex, true);
                type = "finish";
            }
            // Capture logic: check if another player's token is at 'end' and not in a safe zone
            for (String otherPlayer : playerTokenPositions.keySet()) {
                if (otherPlayer.equals(playerName)) continue;
                List<Integer> otherPositions = playerTokenPositions.get(otherPlayer);
                List<Boolean> otherHome = playerTokenHome.get(otherPlayer);
                List<Boolean> otherFinished = playerTokenFinished.get(otherPlayer);
                for (int i = 0; i < otherPositions.size(); i++) {
                    if (!otherHome.get(i) && !otherFinished.get(i) && otherPositions.get(i) == end && !isSafeZone(end)) {
                        // Send captured token home
                        otherHome.set(i, true);
                        otherPositions.set(i, -1);
                        type = "capture";
                    }
                }
            }
            positions.set(tokenIndex, end);
        }
        return new MoveResult(tokenIndex, start, end, type);
    }

    // Helper: define safe zones (example: every 8th position is safe)
    private boolean isSafeZone(int pos) {
        int[] safeZones = {0, 8, 13, 21, 26, 34, 39, 47};
        for (int safe : safeZones) {
            if (pos == safe) return true;
        }
        return false;
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