package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.board.NewBoardGame;

import java.util.Scanner;

/**
 * Handles the user interface for the board game.
 */
public class UserInterface {
    private final NewBoardGame game;
    private final Scanner scanner;

    public UserInterface() {
        this.game = new NewBoardGame(30, 2); // 30 tiles, 2 dice
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("Welcome to the Board Game!");
        System.out.println("Rules:");
        System.out.println("1. Each player takes turns rolling the dice");
        System.out.println("2. Move the number of spaces shown on the dice");
        System.out.println("3. First player to reach the end wins!");
        System.out.println();

        setupPlayers();
        game.startGame();
        playGame();
    }

    private void setupPlayers() {
        int numPlayers = 0;
        while (numPlayers < 2 || numPlayers > 4) {
            try {
                System.out.print("Enter number of players (2-4): ");
                numPlayers = Integer.parseInt(scanner.nextLine().trim());
                if (numPlayers < 2 || numPlayers > 4) {
                    System.out.println("Please enter a number between 2 and 4.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            }
        }

        for (int i = 0; i < numPlayers; i++) {
            String name;
            do {
                System.out.print("Enter name for Player " + (i + 1) + ": ");
                name = scanner.nextLine().trim();
                if (name.isEmpty()) {
                    System.out.println("Name cannot be empty.");
                }
            } while (name.isEmpty());
            game.addPlayer(name);
        }
    }

    private void playGame() {
        while (!game.isGameOver()) {
            Player currentPlayer = game.getCurrentPlayer();
            System.out.println("\n" + currentPlayer.getName() + "'s turn");
            
            System.out.println("Press Enter to roll the dice...");
            scanner.nextLine();
            
            boolean continueGame = game.makeMove();
            int[] diceValues = game.getCurrentDiceValues();
            System.out.println("Rolled: " + diceValues[0] + " + " + diceValues[1] + " = " + (diceValues[0] + diceValues[1]));
            System.out.println("Current position: " + currentPlayer.getCurrentPosition());
            printGameState();
            
            if (!continueGame) {
                Player winner = game.getWinner();
                if (winner != null) {
                    System.out.println("\n" + winner.getName() + " wins!");
                }
            }
        }
    }

    private void printGameState() {
        System.out.println("\nCurrent Game State:");
        for (Player player : game.getPlayers()) {
            System.out.println(player.getName() + " is at position " + player.getCurrentPosition());
        }
    }
} 