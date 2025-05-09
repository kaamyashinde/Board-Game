package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;

import java.util.Map;

/**
 * Console implementation of the GameUI interface.
 */
public class ConsoleGameUI implements GameUI {
    private final ConsoleInputHandler inputHandler;
    private final ConsoleOutputHandler outputHandler;
    private final BoardGame boardGame;

    public ConsoleGameUI(BoardGame boardGame) {
        this.boardGame = boardGame;
        this.inputHandler = new ConsoleInputHandler();
        this.outputHandler = new ConsoleOutputHandler();
    }

    @Override
    public void displayWelcomeMessage() {
        outputHandler.displayWelcomeMessage();
    }

    @Override
    public int getNumberOfPlayers() {
        return inputHandler.getNumberOfPlayers();
    }

    @Override
    public String getPlayerName(int playerNumber) {
        return inputHandler.getPlayerName(playerNumber);
    }

    @Override
    public void displayPlayerTurn(Player player) {
        outputHandler.displayPlayerTurn(player);
    }

    @Override
    public void displayDiceRoll(Player player, int rollResult) {
        outputHandler.displayDiceRoll(player, rollResult);
    }

    @Override
    public void displayBoard() {
        // Update board and player positions in output handler
        outputHandler.setBoard(boardGame.getBoard());
        outputHandler.setPlayerPositions(boardGame.getPlayers());
        
        // Display the board
        outputHandler.displayBoard();
    }

    @Override
    public void displayWinner(Player winner) {
        outputHandler.displayWinner(winner);
    }

    @Override
    public void displaySeparator() {
        outputHandler.displaySeparator();
    }

    /**
     * Displays information about a tile action that was triggered.
     * @param player The player who triggered the action
     * @param action The action that was triggered
     */
    @Override
    public void displayTileAction(Player player, TileAction action) {
        outputHandler.displayTileAction(player, action);
    }

    public void close() {
        inputHandler.close();
    }
} 