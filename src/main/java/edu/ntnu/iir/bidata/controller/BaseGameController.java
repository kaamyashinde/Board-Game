package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;

import java.util.List;
import java.util.logging.Logger;
import lombok.Getter;
import lombok.Setter;

/**
 * Base controller class that contains common game functionality.
 * This class serves as a foundation for specific game controllers.
 */
public abstract class BaseGameController {
    protected static final Logger LOGGER = Logger.getLogger(BaseGameController.class.getName());
    protected BoardGame boardGame;
    @Getter
    @Setter
    protected int currentPlayerIndex = 0;
    protected List<String> playerNames;
    @Getter
    @Setter
    protected boolean diceRolled = false;

    public BaseGameController(BoardGame boardGame) {
        this.boardGame = boardGame;
        LOGGER.info("BaseGameController initialized");
    }

    /**
     * Sets the player names for the game
     */
    public void setPlayerNames(List<String> playerNames) {
        this.playerNames = playerNames;
        LOGGER.info("Setting player names: " + playerNames);
    }

    /**
     * Starts the game
     */
    public void startGame() {
        LOGGER.info("Starting new game");
        boardGame.startGame();
    }

    /**
     * Gets the current player's name
     */
    public String getCurrentPlayerName() {
        return playerNames.get(currentPlayerIndex);
    }

    /**
     * Moves to the next player
     */
    protected void nextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % playerNames.size();
        diceRolled = false;
    }

    /**
     * Abstract method to handle player movement
     * Must be implemented by specific game controllers
     */
    public abstract void handlePlayerMove();
} 