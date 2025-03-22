package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.ui.GameUI;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
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
    private final Board board;
    private final Dice dice;
    private final Map<Player, Integer> players;
    private final GameUI ui;
    private Player currentPlayer;
    private boolean playing;

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
        this.board = new Board(sizeOfBoard);
        this.ui = ui;
        this.playing = false;
    }

    /**
     * Adds a player to the game.
     *
     * @param player the player to add
     * @throws GameException if the game has already started or player is null
     */
    public void addPlayer(Player player) {
        ParameterValidation.validatePlayer(player);
        ParameterValidation.validateGameNotStarted(playing);
        
        player.setCurrentTile(board.getTiles().get(0));
        this.players.put(player, 0);
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
    }

    /**
     * Executes the current player's turn.
     *
     * @throws GameException if the game is not in progress or no current player
     */
    public void playCurrentPlayer() {
        ParameterValidation.validateGameState(playing, currentPlayer);

        ui.displayTurnStart(currentPlayer, currentPlayer.getCurrentTile().getId());

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
        players.put(currentPlayer, currentTile.getId());
        ui.displayNewPosition(currentTile.getId());
    }

    /**
     * Handles the win condition when a player reaches the final tile.
     */
    private void handleGameWin() {
        ui.displayWinner(currentPlayer);
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
}
