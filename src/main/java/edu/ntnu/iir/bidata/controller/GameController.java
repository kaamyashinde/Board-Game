package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.GameUI;

/**
 * Controls the flow of the game and coordinates between the model and view.
 */
public class GameController {
    private final BoardGame boardGame;
    private final GameUI gameUI;

    public GameController(BoardGame boardGame, GameUI gameUI) {
        this.boardGame = boardGame;
        this.gameUI = gameUI;
    }

    public void startGame() {
        // Display welcome message
        gameUI.displayWelcomeMessage();
        
        // Initialize players
        initializePlayers();
        
        // Start the game
        boardGame.initialiseGame();
        
        // Play the game
        playGame();
    }

    private void initializePlayers() {
        // Get number of players
        int numPlayers = gameUI.getNumberOfPlayers();
        
        // Create and add players
        for (int i = 0; i < numPlayers; i++) {
            String playerName = gameUI.getPlayerName(i + 1);
            Player player = new Player(playerName);
            boardGame.addPlayer(player);
        }
    }

    private void playGame() {
        while (!boardGame.isGameOver()) {
            // Get current player
            Player currentPlayer = boardGame.getCurrentPlayer();
            
            // Display current player's turn
            gameUI.displayPlayerTurn(currentPlayer);
            gameUI.displaySeparator();
            
            // Roll dice
            int diceRoll = boardGame.rollDice();
            gameUI.displayDiceRoll(currentPlayer, diceRoll);
            
            // Move player
            boolean hasWon = boardGame.movePlayer(currentPlayer, diceRoll);
            
            // Display board state
            gameUI.displayBoard();
            gameUI.displaySeparator();
            
            // Check if game is over
            if (hasWon) {
                gameUI.displayWinner(currentPlayer);
                break;
            }
            
            // Move to next player
            boardGame.nextPlayer();
        }
    }
} 