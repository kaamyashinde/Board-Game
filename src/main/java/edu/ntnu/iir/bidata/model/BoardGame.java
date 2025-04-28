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
    private final List<BoardGameObserver> observers;
    private Player currentPlayer;
    private boolean playing;
    private final int sizeOfBoard;

    /**
     * Constructor that initializes the board game with the specified parameters.
     *
     * @param numOfDices   number of dice to use in the game
     * @param sizeOfBoard  size of the game board
     * @throws GameException if parameters are invalid
     */
    public BoardGame(int numOfDices, int sizeOfBoard) {
        ParameterValidation.validateGameParameters(numOfDices, 2, sizeOfBoard); // Default to 2 players minimum
        
        this.dice = new Dice(numOfDices);
        this.players = new HashMap<>();
        this.observers = new ArrayList<>();
        this.playing = false;
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
        ParameterValidation.validateGameNotStarted(playing);
        
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
        ParameterValidation.validateGameNotStarted(playing);
        
        currentPlayer = players.keySet().iterator().next();
        playing = true;
        notifyTurnChanged(currentPlayer);
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
        notifyPlayerMoved(player, newPosition);

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
        notifyGameWon(winner);
        playing = false;
    }

    /**
     * Checks if the game is over.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return !playing;
    }

    /**
     * Gets the current player.
     *
     * @return the current player
     */
    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    /**
     * Moves to the next player's turn.
     */
    public void nextPlayer() {
        List<Player> playerList = new ArrayList<>(players.keySet());
        int currentIndex = playerList.indexOf(currentPlayer);
        int nextIndex = (currentIndex + 1) % playerList.size();
        currentPlayer = playerList.get(nextIndex);
        notifyTurnChanged(currentPlayer);
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
        Objects.requireNonNull(observer, "Observer cannot be null");
        observers.add(observer);
    }

    /**
     * Removes an observer from the list of observers.
     *
     * @param observer the observer to remove
     */
    public void removeObserver(BoardGameObserver observer) {
        observers.remove(observer);
    }

    /**
     * Notifies all observers that a player has moved.
     *
     * @param player the player who moved
     * @param newPosition the new position
     */
    private void notifyPlayerMoved(Player player, int newPosition) {
        for (BoardGameObserver observer : observers) {
            observer.onPlayerMoved(player, newPosition);
        }
    }

    /**
     * Notifies all observers that a player has won.
     *
     * @param winner the winning player
     */
    private void notifyGameWon(Player winner) {
        for (BoardGameObserver observer : observers) {
            observer.onGameWon(winner);
        }
    }

    /**
     * Notifies all observers that the turn has changed.
     *
     * @param currentPlayer the player whose turn it is now
     */
    private void notifyTurnChanged(Player currentPlayer) {
        for (BoardGameObserver observer : observers) {
            observer.onTurnChanged(currentPlayer);
        }
    }
}
