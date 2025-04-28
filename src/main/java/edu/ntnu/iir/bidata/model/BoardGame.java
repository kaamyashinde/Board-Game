package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.ui.GameUI;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
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
    private final GameUI ui;
    private final List<BoardGameObserver> observers;
    private Player currentPlayer;
    private boolean playing;
    private final int sizeOfBoard;

    /**
     * Constructor that initializes the board game with the specified parameters.
     *
     * @param numOfDices   number of dice to use in the game
     * @param numOfPlayers number of players in the game
     * @param sizeOfBoard  size of the game board
     * @param ui          the UI implementation to use
     * @throws GameException if parameters are invalid
     */
    public BoardGame(int numOfDices, int numOfPlayers, int sizeOfBoard, GameUI ui) {
        ParameterValidation.validateGameParameters(numOfDices, numOfPlayers, sizeOfBoard);
        Objects.requireNonNull(ui, "UI cannot be null");
        
        this.dice = new Dice(numOfDices);
        this.players = new HashMap<>(numOfPlayers);
        this.ui = ui;
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
        ui.displaySeparator();
        ui.displayTurnStart(currentPlayer, currentPlayer.getCurrentTile().getId());
        notifyTurnChanged(currentPlayer);
    }

    /**
     * Executes the current player's turn.
     *
     * @throws GameException if the game is not in progress or no current player
     */
    public void playCurrentPlayer() {
        ParameterValidation.validateGameState(playing, currentPlayer);

        ui.displayTurnStart(currentPlayer, currentPlayer.getCurrentTile().getId());
        notifyTurnChanged(currentPlayer);

        dice.rollAllDice();
        int steps = dice.sumOfRolledValues();
        ui.displayDiceRoll(steps);

        movePlayer(steps);
    }

    /**
     * Moves the current player the specified number of steps.
     *
     * @param steps number of steps to move
     */
    private void movePlayer(int steps) {
        Tile currentTile = currentPlayer.getCurrentTile();
        for (int i = 0; i < steps; i++) {
            if (currentTile.getNextTile() == null) {
                handleGameWin();
                return;
            }
            currentTile = currentTile.getNextTile();
        }
        
        currentPlayer.setCurrentTile(currentTile);
        int newPosition = currentTile.getId();
        players.put(currentPlayer, newPosition);
        ui.displayNewPosition(newPosition);
        notifyPlayerMoved(currentPlayer, newPosition);

        // Check for and display any tile action
        TileAction action = currentTile.getAction();
        if (action != null) {
            ui.displayTileAction(currentPlayer, action);
            action.performAction(currentPlayer);
        }
    }

    /**
     * Handles the win condition when a player reaches the final tile.
     */
    private void handleGameWin() {
        ui.displayWinner(currentPlayer);
        notifyGameWon(currentPlayer);
        playing = false;
    }

    /**
     * Starts and runs the game until a winner is determined.
     *
     * @throws GameException if the game has not been initialized
     */
    public void playGame() {
        if (!playing) {
            throw new GameException("Game has not been initialized");
        }

        while (playing) {
            for (Map.Entry<Player, Integer> playerEntry : players.entrySet()) {
                currentPlayer = playerEntry.getKey();
                playCurrentPlayer();
                if (!playing) {
                    break;
                }
            }
        }
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
