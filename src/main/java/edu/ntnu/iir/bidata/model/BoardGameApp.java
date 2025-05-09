package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.view.ConsoleGameUI;
import edu.ntnu.iir.bidata.view.GameUI;

import lombok.Getter;
import lombok.NonNull;

/**
 * * Application class that interacts with the BoardGame facade.
 *  * This provides a high-level interface between the user interface and the game logic.
 *
 *
 * @author Durva
 * @version 1.0.0
 */

@Getter
public class BoardGameApp {
  private BoardGame boardGame;
  private final GameUI ui;


  /**
   * Constructor that initializes the application with a console UI by default.
   */
  public BoardGameApp() {
    this.ui = new ConsoleGameUI();
    this.boardGame = null; // Will be initialized when creating a game
  }
  /**
   * Constructor that allows specifying a custom UI implementation.
   *
   * @param ui the UI implementation to use
   */
  public BoardGameApp(@NonNull GameUI ui) {
    this.ui = ui;
    this.boardGame = null; // Will be initialized when creating a game
  }

  /**
   * Creates a new game with the specified parameters.
   *
   * @param numDice number of dice for the game
   * @param numPlayers number of players for the game
   * @param boardSize size of the board
   * @return true if the game was created successfully, false otherwise
   */
  public boolean createGame(int numDice, int numPlayers, int boardSize) {
    try {
      this.boardGame = new BoardGame(numDice, numPlayers, boardSize, ui);
      return true;
    } catch (GameException e) {
      displayError("Failed to create game: " + e.getMessage());
      return false;
    }
  }

  /**
   * Adds a player to the game.
   *
   * @param playerName the name of the player to add
   * @return true if the player was added successfully, false otherwise
   */
  public boolean addPlayer(@NonNull String playerName) {
    if (boardGame == null) {
      displayError("Game not created yet");
      return false;
    }

    try {
      Player player = new Player(playerName);
      boardGame.addPlayer(player);
      return true;
    } catch (GameException e) {
      displayError("Failed to add player: " + e.getMessage());
      return false;
    }
  }

  /**
   * Initializes and starts the game.
   *
   * @return true if the game was started successfully, false otherwise
   */
  public boolean startGame() {
    if (boardGame == null) {
      displayError("Game not created yet");
      return false;
    }

    try {
      boardGame.initialiseGame();
      return true;
    } catch (GameException e) {
      displayError("Failed to start game: " + e.getMessage());
      return false;
    }
  }

  /**
   * Plays the game from start to finish.
   *
   * @return true if the game completed successfully, false otherwise
   */
  public boolean playFullGame() {
    if (boardGame == null) {
      displayError("Game not created yet");
      return false;
    }

    try {
      boardGame.playGame();
      return true;
    } catch (GameException e) {
      displayError("Failed to play game: " + e.getMessage());
      return false;
    }
  }

  /**
   * Plays a single turn for the current player.
   *
   * @return true if the turn was played successfully, false otherwise
   */
  public boolean playTurn() {
    if (boardGame == null) {
      displayError("Game not created yet");
      return false;
    }

    try {
      boardGame.playCurrentPlayer();
      return true;
    } catch (GameException e) {
      displayError("Failed to play turn: " + e.getMessage());
      return false;
    }
  }

  /**
   * Sets up and runs a complete game with the specified parameters.
   *
   * @param numDice number of dice
   * @param boardSize size of the board
   * @param playerNames array of player names
   * @return true if the game completed successfully, false otherwise
   */
  public boolean setupAndPlayGame(int numDice, int boardSize, @NonNull String[] playerNames) {
    // Create game
    if (!createGame(numDice, playerNames.length, boardSize)) {
      return false;
    }

    // Add players
    for (String name : playerNames) {
      if (!addPlayer(name)) {
        return false;
      }
    }

    // Display game setup info
    System.out.println("=== Board Game Setup ===");
    System.out.println("Board Size: " + boardSize);
    System.out.println("Number of Dice: " + numDice);
    System.out.println("Players:");
    for (int i = 0; i < playerNames.length; i++) {
      System.out.println((i + 1) + ". " + playerNames[i]);
    }
    System.out.println("=====================\n");

    // Start and play the game
    if (!startGame()) {
      return false;
    }

    return playFullGame();
  }

  /**
   * Checks if the game is currently active.
   *
   * @return true if the game is active, false otherwise
   */
  public boolean isGameActive() {
    return boardGame != null && boardGame.isPlaying();
  }

  /**
   * Displays an error message.
   *
   * @param message the error message to display
   */
  private void displayError(@NonNull String message) {
    System.err.println("ERROR: " + message);
  }
}