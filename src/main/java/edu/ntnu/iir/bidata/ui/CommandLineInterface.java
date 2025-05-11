package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.NewBoardGame;
import edu.ntnu.iir.bidata.model.Player;
import java.util.Scanner;
import java.util.List;

public class CommandLineInterface {
    private NewBoardGame game;
    private final Scanner scanner;

    public CommandLineInterface(NewBoardGame game) {
        this.game = game;
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
        int round = 1;
        while (!game.isGameOver()) {
            if (game.getCurrentPlayerIndex() == 0) {
                System.out.println("\n=== Round " + round + " ===");
                round++;
            }
            Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
            if (currentPlayer.isSkipNextTurn()) {
                System.out.println(currentPlayer.getName() + " must skip their turn!");
                game.makeMove();
                continue;
            }
            System.out.println("\n" + currentPlayer.getName() + "'s turn");
            System.out.println("Current position: " + currentPlayer.getCurrentPosition());
            System.out.print("Press Enter to roll the dice...");
            scanner.nextLine();
            int[] diceValues = game.getCurrentDiceValues();
            game.makeMove();
            int steps = 0;
            for (int v : diceValues) steps += v;
            if (diceValues.length > 0) {
                System.out.print("Rolled: ");
                for (int i = 0; i < diceValues.length; i++) {
                    System.out.print(diceValues[i]);
                    if (i < diceValues.length - 1) System.out.print(" + ");
                }
                System.out.println(" = " + steps);
            }
            Player updatedPlayer = game.getPlayers().get(game.getCurrentPlayerIndex() == 0 ? game.getPlayers().size() - 1 : game.getCurrentPlayerIndex() - 1);
            System.out.println("New position: " + updatedPlayer.getCurrentPosition());
            printGameState();
        }
        Player winner = game.getWinner();
        if (winner != null) {
            System.out.println("\n" + winner.getName() + " wins!");
        }
        System.out.println("Game Over!");
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

    private void printGameState() {
        System.out.println("\nCurrent Game State:");
        List<Player> players = game.getPlayers();
        for (Player player : players) {
            System.out.println(player.getName() + " is at position " + player.getCurrentPosition());
        }
    }
} 