package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.GameUI;
import edu.ntnu.iir.bidata.view.JavaFXGameUI;

/**
 * Controls the flow of the game and coordinates between the model and view.
 */
public class GameController {
    private final BoardGame boardGame;
    private final GameUI gameUI;
    private boolean gameStarted = false;

    public GameController(BoardGame boardGame, GameUI gameUI) {
        this.boardGame = boardGame;
        this.gameUI = gameUI;
        
        // Set up the next turn action if using JavaFX UI
        if (gameUI instanceof JavaFXGameUI) {
            ((JavaFXGameUI) gameUI).setNextTurnAction(this::handleNextTurn);
        }
    }

    public void startGame() {
        if (!gameStarted) {
            // Display welcome message
            gameUI.displayWelcomeMessage();
            
            // Display initial board state
            gameUI.displayBoard();
            
            // Start the first turn
            startNextTurn();
            
            gameStarted = true;
        }
    }

    private void startNextTurn() {
        if (!boardGame.isGameOver()) {
            Player currentPlayer = boardGame.getCurrentPlayer();
            gameUI.displayPlayerTurn(currentPlayer);
        }
    }

    private void handleNextTurn() {
        if (!boardGame.isGameOver()) {
            Player currentPlayer = boardGame.getCurrentPlayer();
            
            // Roll dice
            int diceRoll = boardGame.rollDice();
            gameUI.displayDiceRoll(currentPlayer, diceRoll);
            
            // Move player
            boolean hasWon = boardGame.movePlayer(currentPlayer, diceRoll);
            
            // Display board state
            gameUI.displayBoard();
            
            // Check if game is over
            if (hasWon) {
                gameUI.displayWinner(currentPlayer);
            } else {
                // Move to next player
                boardGame.nextPlayer();
                startNextTurn();
            }
        }
    }
} 