package edu.ntnu.iir.bidata.view.cli;

import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The CommandLineInterface class provides a command-line-based interface for playing a board game.
 * It handles the initialization of the game, facilitates player interactions, and manages game
 * flow. The class utilizes a {@link Scanner} to receive user inputs and displays game progress
 * through the console.
 *
 * <p>Key Features: - Allows players to interact with the board game through text-based inputs. -
 * Manages the game flow, including player turns, dice rolls, and tile actions. - Prints the current
 * game state, round results, and the final winner of the game. - Supports a setup phase to register
 * players. - Logs game progress and events for better debugging.
 *
 * <p>Dependencies: - Requires a {@link BoardGame} instance to handle game-related operations such
 * as player moves, tile actions, and determining the game state.
 */
public class CommandLineInterface {

  private static final Logger LOGGER = Logger.getLogger(CommandLineInterface.class.getName());
  private final Scanner scanner;
  private final BoardGame game;

  /**
   * Initializes a new instance of the CommandLineInterface for a given board game.
   *
   * @param game the BoardGame instance that this interface will interact with
   */
  public CommandLineInterface(BoardGame game) {
    this.game = game;
    this.scanner = new Scanner(System.in);
    LOGGER.info("CommandLineInterface initialized");
  }

  /**
   * Starts the Command Line Interface (CLI) session for the board game.
   *
   * <p>This method acts as the main entry point for interacting with the game via the CLI. It
   * initializes game settings, manages player turns, and displays game progress and results in real
   * time. The method includes:
   *
   * <p>- Welcoming the player and displaying the game rules. - Setting up players via user input. -
   * Initiating and controlling the main game loop. - Handling player actions such as rolling dice
   * and updating positions. - Displaying round summaries and the final winner at the end of the
   * game.
   *
   * <p>The method remains in a loop until the game condition is met (game over). Player actions
   * such as dice rolls and tile-specific events or actions during each turn are logged and
   * displayed to the players.
   *
   * <p>This method also handles edge cases such as ensuring the minimum number of players required
   * to start the game and handling tile-based actions or consequences based on the game rules.
   */
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
        java.util.stream.IntStream.range(0, moveResult.diceValues.length)
            .forEach(
                i -> {
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
          new TurnResult(
              round,
              moveResult.playerName,
              diceStr.toString(),
              moveResult.prevPos,
              moveResult.posAfterMove,
              moveResult.posAfterAction,
              moveResult.actionDesc));
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

  /**
   * Sets up the players for the game by gathering their names and ensuring a valid number of
   * players is entered.
   *
   * <p>The method interacts with the user via console input to: - Prompt for the number of players,
   * which must be between 2 and 4 inclusive. - Validate the input and request re-entry if the
   * number is out of range or invalid (non-numeric or empty). - Prompt each player to enter their
   * name, ensuring that no empty names are allowed.
   *
   * <p>Once valid input is provided, the players’ names are added to the game. Logging is used to
   * capture information about the setup and any invalid input encountered.
   *
   * <p>Preconditions: - The `scanner` object must be initialized for user input. - The `game`
   * object must be initialized and ready to accept players.
   *
   * <p>Postconditions: - The game is populated with valid player names. - The setup process is
   * logged, including warnings for invalid inputs.
   */
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
    java.util.stream.IntStream.range(0, numPlayers)
        .forEach(
            i -> {
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

  /**
   * Prints a formatted table displaying the results of a single round in the game.
   *
   * <p>The table includes each player's turn results, such as the dice rolled, positions before and
   * after movements, and details of any tile actions encountered.
   *
   * <p>The table includes the following columns: - Round number - Player name - Dice rolled -
   * Previous position - New position after the move - Final position after applying the tile action
   * - Description of the tile action
   *
   * @param round the round number for which the results are being displayed
   * @param turnResults a list of TurnResult objects containing the data for each player's turn in
   *     the round
   */
  private void printTurnResultsTable(int round, List<TurnResult> turnResults) {
    System.out.println("\nResults for Round " + round + ":");
    System.out.printf(
        "%-10s %-12s %-15s %-15s %-22s %-22s %-20s\n",
        "Round",
        "Player",
        "Dice Rolled",
        "Prev Pos",
        "After Move",
        "After Tile Action",
        "Tile Action");
    turnResults.forEach(
        tr -> {
          System.out.printf(
              "%-10d %-12s %-15s %-15d %-22d %-22d %-20s\n",
              tr.round,
              tr.playerName,
              tr.diceRolled,
              tr.prevPos,
              tr.newPos,
              tr.afterActionPos,
              tr.actionDesc);
        });
  }

  /**
   * Prints the current state of the game, including the positions of all players.
   *
   * <p>This method retrieves the list of players from the associated game object and prints each
   * player's name and current position to the console. It also logs this information for debugging
   * or tracking purposes. The output begins with a header message indicating the current game
   * state.
   *
   * <p>Preconditions: - The `game` object is initialized and contains a valid list of players. -
   * The `LOGGER` object is initialized for logging purposes.
   *
   * <p>Postconditions: - The current game state, including player positions, is printed to the
   * console. - Player positions and corresponding information are logged.
   *
   * <p>Side Effects: - Outputs text to the console. - Writes log entries for each player's name and
   * position.
   */
  private void printGameState() {
    LOGGER.info("Printing current game state");
    System.out.println("\nCurrent Game State:");
    List<Player> players = game.getPlayers();
    players.forEach(
        player -> {
          System.out.println(player.getName() + " is at position " + player.getCurrentPosition());
          LOGGER.info(player.getName() + " position: " + player.getCurrentPosition());
        });
  }

  /**
   * Represents the result of a single player's turn in a board game.
   *
   * <p>This class encapsulates the details of a turn, including: - The round number in which the
   * turn occurred. - The player's name who took the turn. - The dice value rolled during the turn.
   * - The player's position prior to the move. - The player's new position after the dice roll and
   * movement. - The player's final position after any actions or tile effects were applied. - A
   * description of the action or effect applied during the turn.
   *
   * <p>Instances of this class are typically used to store and display the outcomes of individual
   * turns in the game.
   */
  private static class TurnResult {

    int round;
    String playerName;
    String diceRolled;
    int prevPos;
    int newPos;
    int afterActionPos;
    String actionDesc;

    /**
     * Constructs a TurnResult object representing the result of a player's turn.
     *
     * @param round The round number in which the turn occurred.
     * @param playerName The name of the player who took the turn.
     * @param diceRolled The value(s) rolled on the dice during the turn.
     * @param prevPos The player's position before the dice roll.
     * @param newPos The player's position after moving based on the dice roll.
     * @param afterActionPos The player's position after applying any tile-specific actions or
     *     effects.
     * @param actionDesc A description of any action or event that occurred during the turn.
     */
    TurnResult(
        int round,
        String playerName,
        String diceRolled,
        int prevPos,
        int newPos,
        int afterActionPos,
        String actionDesc) {
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
