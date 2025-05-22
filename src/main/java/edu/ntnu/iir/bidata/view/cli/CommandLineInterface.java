package edu.ntnu.iir.bidata.view.cli;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CommandLineInterface {

  private static final Logger LOGGER = Logger.getLogger(CommandLineInterface.class.getName());
  private final Scanner scanner;
  private final BoardGame game;

  public CommandLineInterface(BoardGame game) {
    this.game = game;
    this.scanner = new Scanner(System.in);
    LOGGER.info("CommandLineInterface initialized");
  }

  public void start() {
    LOGGER.info("Starting CLI game session");
    System.out.println("Welcome to the Board Game!");
    System.out.println("Rules:");
    System.out.println("1. Each player takes turns rolling the dice");
    System.out.println("2. Move the number of spaces shown on the dice");
    System.out.println("3. First player to reach the end wins!");
    System.out.println();

    setupPlayers();
    game.startGame();
    LOGGER.info("Game started");
    int round = 1;
    ArrayList<TurnResult> turnResults = new ArrayList<>();
    while (!game.isGameOver()) {
      if (game.getCurrentPlayerIndex() == 0 && !turnResults.isEmpty()) {
        printTurnResultsTable(round - 1, turnResults);
        turnResults.clear();
        System.out.println("\n=== Round " + round + " ===");
        round++;
      }
      Player currentPlayer = game.getPlayers().get(game.getCurrentPlayerIndex());
      System.out.println("\n" + currentPlayer.getName() + "'s turn");
      System.out.println("Current position: " + currentPlayer.getCurrentPosition());
      System.out.print("Press Enter to roll the dice...");
      scanner.nextLine();
      BoardGame.MoveResult moveResult = game.makeMoveWithResult();
      StringBuilder diceStr = new StringBuilder();
      int[] stepsArr = {0};
      if (moveResult.diceValues != null && moveResult.diceValues.length > 0) {
        java.util.stream.IntStream.range(0, moveResult.diceValues.length).forEach(i -> {
          stepsArr[0] += moveResult.diceValues[i];
          diceStr.append(moveResult.diceValues[i]);
          if (i < moveResult.diceValues.length - 1) {
            diceStr.append(" + ");
          }
        });
        System.out.println("Rolled: " + diceStr + " = " + stepsArr[0]);
      }
      if (moveResult.actionDesc != null && !moveResult.actionDesc.isEmpty()) {
        System.out.println("Tile Action: " + moveResult.actionDesc);
      }
      System.out.println("New position after move: " + moveResult.posAfterMove);
      System.out.println("New position after tile action: " + moveResult.posAfterAction);
      turnResults.add(
          new TurnResult(round, moveResult.playerName, diceStr.toString(), moveResult.prevPos,
              moveResult.posAfterMove, moveResult.posAfterAction, moveResult.actionDesc));
      printGameState();
    }
    // Print the last round's table
    if (!turnResults.isEmpty()) {
      printTurnResultsTable(round - 1, turnResults);
    }
    Player winner = game.getWinner();
    if (winner != null) {
      LOGGER.info("Game ended. Winner: " + winner.getName());
      System.out.println("\n" + winner.getName() + " wins!");
    }
    System.out.println("Game Over!");
  }

  private void setupPlayers() {
    LOGGER.info("Starting player setup");
    int numPlayers = 0;
    while (numPlayers < 2 || numPlayers > 4) {
      try {
        System.out.print("Enter number of players (2-4): ");
        numPlayers = Integer.parseInt(scanner.nextLine().trim());
        if (numPlayers < 2 || numPlayers > 4) {
          LOGGER.warning("Invalid number of players entered: " + numPlayers);
          System.out.println("Please enter a number between 2 and 4.");
        }
      } catch (NumberFormatException e) {
        LOGGER.log(Level.WARNING, "Invalid input for number of players", e);
        System.out.println("Please enter a valid number.");
      }
    }
    java.util.stream.IntStream.range(0, numPlayers).forEach(i -> {
      String name;
      do {
        System.out.print("Enter name for Player " + (i + 1) + ": ");
        name = scanner.nextLine().trim();
        if (name.isEmpty()) {
          LOGGER.warning("Empty player name entered");
          System.out.println("Name cannot be empty.");
        }
      } while (name.isEmpty());
      game.addPlayer(name);
      LOGGER.info("Added player: " + name);
    });
    LOGGER.info("Player setup completed with " + numPlayers + " players");
  }

  // Print a table of turn results for a round
  private void printTurnResultsTable(int round, List<TurnResult> turnResults) {
    System.out.println("\nResults for Round " + round + ":");
    System.out.printf("%-10s %-12s %-15s %-15s %-22s %-22s %-20s\n", "Round", "Player",
        "Dice Rolled", "Prev Pos", "After Move", "After Tile Action", "Tile Action");
    turnResults.forEach(tr -> {
      System.out.printf("%-10d %-12s %-15s %-15d %-22d %-22d %-20s\n", tr.round, tr.playerName,
          tr.diceRolled, tr.prevPos, tr.newPos, tr.afterActionPos, tr.actionDesc);
    });
  }

  private void printGameState() {
    LOGGER.info("Printing current game state");
    System.out.println("\nCurrent Game State:");
    List<Player> players = game.getPlayers();
    players.forEach(player -> {
      System.out.println(player.getName() + " is at position " + player.getCurrentPosition());
      LOGGER.info(player.getName() + " position: " + player.getCurrentPosition());
    });
  }

  // Helper class to store turn results
  private static class TurnResult {

    int round;
    String playerName;
    String diceRolled;
    int prevPos;
    int newPos;
    int afterActionPos;
    String actionDesc;

    TurnResult(int round, String playerName, String diceRolled, int prevPos, int newPos,
        int afterActionPos, String actionDesc) {
      this.round = round;
      this.playerName = playerName;
      this.diceRolled = diceRolled;
      this.prevPos = prevPos;
      this.newPos = newPos;
      this.afterActionPos = afterActionPos;
      this.actionDesc = actionDesc;
    }
  }
} 