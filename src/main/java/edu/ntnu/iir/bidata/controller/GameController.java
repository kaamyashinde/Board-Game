package edu.ntnu.iir.bidata.controller;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.view.GameUI;

public class GameController {
    private BoardGame boardGame;
    private GameUI gameUI;

    public GameController(BoardGame boardGame, GameUI gameUI) {
        this.boardGame = boardGame;
        this.gameUI = gameUI;
    }

    public void startGame() {
        gameUI.displayWelcomeMessage();
        initializePlayers();
        playGame();
    }

    private void initializePlayers() {
        int numPlayers = gameUI.getNumberOfPlayers();
        for (int i = 0; i < numPlayers; i++) {
            String playerName = gameUI.getPlayerName(i + 1);
            boardGame.addPlayer(new Player(playerName));
        }
    }

    private void playGame() {
        while (!boardGame.isGameOver()) {
            Player currentPlayer = boardGame.getCurrentPlayer();
            gameUI.displayPlayerTurn(currentPlayer);
            
            // Handle player's turn
            int diceRoll = boardGame.rollDice();
            gameUI.displayDiceRoll(currentPlayer, diceRoll);
            
            boardGame.movePlayer(currentPlayer, diceRoll);
            gameUI.displayBoard();
            
            // Check for game over condition
            if (boardGame.hasPlayerWon(currentPlayer)) {
                gameUI.displayWinner(currentPlayer);
                break;
            }
            
            boardGame.nextPlayer();
        }
    }
} 