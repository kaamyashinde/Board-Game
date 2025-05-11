package edu.ntnu.iir.bidata.ui;

import edu.ntnu.iir.bidata.model.board.NewBoardGame;
import edu.ntnu.iir.bidata.model.Player;
import java.util.Scanner;

public class CommandLineInterface {
    private final NewBoardGame game;
    private final Scanner scanner;

    public CommandLineInterface(NewBoardGame game) {
        this.game = game;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
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
            System.out.println("Rolled: " + steps + " steps");
            Player updatedPlayer = game.getPlayers().get(game.getCurrentPlayerIndex() == 0 ? game.getPlayers().size() - 1 : game.getCurrentPlayerIndex() - 1);
            System.out.println("New position: " + updatedPlayer.getCurrentPosition());
        }
        System.out.println("Game Over! Winner: " + game.getWinner().getName());
    }
} 