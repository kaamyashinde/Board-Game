package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * A class for managing the board game.
 *
 * @author kaamyashinde
 * @version 1.0.0
 */
@Getter
@Setter
public class BoardGame {
    private Board board;
    private final Dice dice;
    private final Map<Player, Integer> players;
    private final GameStateManager stateManager;
    private final GameRulesManager rulesManager;
    private final GameEventManager eventManager;
    private final int sizeOfBoard;

    /**
     * Constructor that initializes the board game with the specified parameters.
     *
     * @param numOfDices   number of dice to use in the game
     * @param sizeOfBoard  size of the game board
     * @throws GameException if parameters are invalid
     */
    public BoardGame(int numOfDices, int sizeOfBoard) {
        this.rulesManager = new GameRulesManager();
        this.rulesManager.validateGameParameters(numOfDices, 2, sizeOfBoard);
        
        this.dice = new Dice(numOfDices);
        this.players = new HashMap<>();
        this.stateManager = new GameStateManager(players);
        this.eventManager = new GameEventManager();
        this.sizeOfBoard = sizeOfBoard;
    }

    /**
     * Adds a player to the game and updates the board with the new player list.
     *
     * @param player the player to add
     * @throws GameException if the game has already started or player is null
     */
    public void addPlayer(Player player) {
        ParameterValidation.validatePlayer(player);
        ParameterValidation.validateGameNotStarted(stateManager.isPlaying());
        
        this.players.put(player, 0);
        
        // Create or update the board with the current list of players
        List<Player> playerList = new ArrayList<>(players.keySet());
        if (board == null) {
            board = new Board(sizeOfBoard, playerList);
        }
        
        player.setCurrentTile(board.getTiles().get(0));
    }

    /**
     * Initializes the game and sets the first player.
     *
     * @throws GameException if no players are added or game is already playing
     */
    public void initialiseGame() {
        ParameterValidation.validatePlayersExist(players);
        ParameterValidation.validateGameNotStarted(stateManager.isPlaying());
        
        stateManager.initializeGame();
        eventManager.notifyTurnChanged(stateManager.getCurrentPlayer());
    }

    /**
     * Rolls the dice and returns the result.
     *
     * @return the sum of the dice roll
     */
    public int rollDice() {
        dice.rollAllDice();
        return dice.sumOfRolledValues();
    }

    /**
     * Moves the current player the specified number of steps.
     *
     * @param player the player to move
     * @param steps number of steps to move
     * @return true if the player won, false otherwise
     */
    public boolean movePlayer(Player player, int steps) {
        Tile currentTile = player.getCurrentTile();
        for (int i = 0; i < steps; i++) {
            if (currentTile.getNextTile() == null) {
                handleGameWin(player);
                return true;
            }
            currentTile = currentTile.getNextTile();
        }
        
        player.setCurrentTile(currentTile);
        int newPosition = currentTile.getId();
        players.put(player, newPosition);
        eventManager.notifyPlayerMoved(player, newPosition);

        // Check for and perform any tile action
        TileAction action = currentTile.getAction();
        if (action != null) {
            action.performAction(player);
        }
        
        return false;
    }

    /**
     * Handles the win condition when a player reaches the final tile.
     */
    private void handleGameWin(Player winner) {
        stateManager.setWinner(winner);
        eventManager.notifyGameWon(winner);
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return stateManager.isGameOver();
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return stateManager.getCurrentPlayer();
    }

    /**
     * Moves to the next player's turn.
     */
    public void nextPlayer() {
        stateManager.nextPlayer();
        eventManager.notifyTurnChanged(stateManager.getCurrentPlayer());
    }

    /**
     * Checks if a player has won the game.
     *
     * @param player the player to check
     * @return true if the player has won, false otherwise
     */
    public boolean hasPlayerWon(Player player) {
        return player.getCurrentTile().getNextTile() == null;
    }

    /**
     * Adds an observer to be notified of game state changes.
     *
     * @param observer the observer to add
     * @throws IllegalArgumentException if observer is null
     */
    public void addObserver(BoardGameObserver observer) {
        eventManager.addObserver(observer);
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(BoardGameObserver observer) {
        eventManager.removeObserver(observer);
    }
}
