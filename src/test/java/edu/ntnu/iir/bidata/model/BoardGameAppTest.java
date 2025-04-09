package edu.ntnu.iir.bidata.model;

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.board.BoardGame;
import edu.ntnu.iir.bidata.model.board.BoardGameApp;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.ui.ConsoleGameUI;
import edu.ntnu.iir.bidata.ui.GameUI;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;

import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Test class for BoardGameApp.
 *
 * @author Durva
 */

public class BoardGameAppTest {
  private BoardGameApp app;
  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private final PrintStream originalOut = System.out;
  private final PrintStream originalErr = System.err;

  /**
   * Mock UI implementation for testing
   */
  class MockGameUI implements GameUI {
    private String lastMessage = "";

    @Override
    public void displayTurnStart(Player player, int currentPosition) {
      this.lastMessage = "Turn start: " + player.getName() + " at position " + currentPosition;
    }

    @Override
    public void displayDiceRoll(int rollResult) {
      this.lastMessage = "Rolled: " + rollResult;
    }

    @Override
    public void displayNewPosition(int newPosition) {
      this.lastMessage = "New position: " + newPosition;
    }

    @Override
    public void displayWinner(Player winner) {
      this.lastMessage = "Player " + winner.getName() + " has won!";
    }

    @Override
    public void displaySeparator() {
      this.lastMessage = "-----------";
    }

    public String getLastMessage() {
      return lastMessage;
    }
  }

  /**
   * A mock board game for testing that allows us to control behavior
   */
  class MockBoardGame extends BoardGame {
    private boolean initializeGameCalled = false;
    private boolean playGameCalled = false;
    private boolean playCurrentPlayerCalled = false;
    @Setter
    private boolean throwExceptionOnInit = false;
    @Setter
    private boolean throwExceptionOnPlay = false;
    @Setter
    private boolean throwExceptionOnPlayCurrent = false;

    public MockBoardGame(int numDice, int numPlayers, int boardSize, GameUI ui) throws GameException {
      super(numDice, numPlayers, boardSize, ui);
    }

    @Override
    public void initialiseGame() throws GameException {
      initializeGameCalled = true;
      if (throwExceptionOnInit) {
        throw new GameException("Test exception on init");
      }
    }

    @Override
    public void playGame() throws GameException {
      playGameCalled = true;
      if (throwExceptionOnPlay) {
        throw new GameException("Test exception on play");
      }
    }

    @Override
    public void playCurrentPlayer() throws GameException {
      playCurrentPlayerCalled = true;
      if (throwExceptionOnPlayCurrent) {
        throw new GameException("Test exception on play current");
      }
    }

    public boolean wasInitializeGameCalled() {
      return initializeGameCalled;
    }

    public boolean wasPlayGameCalled() {
      return playGameCalled;
    }

    public boolean wasPlayCurrentPlayerCalled() {
      return playCurrentPlayerCalled;
    }
  }

  @BeforeEach
  public void setup() {
    app = new BoardGameApp();

    // Redirect output streams for testing error messages
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @Test
  public void testConstructor() {
    // Test default constructor
    assertNotNull(app.getUi(), "UI should not be null");
    assertTrue(app.getUi() instanceof ConsoleGameUI, "Default UI should be ConsoleGameUI");
    assertNull(app.getBoardGame(), "BoardGame should be null initially");

    // Test constructor with custom UI
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp customApp = new BoardGameApp(mockUi);
    assertSame(mockUi, customApp.getUi(), "Custom UI should be set");
  }

  @Test
  public void testCreateGame() {
    // Test successful game creation
    boolean result = app.createGame(2, 2, 10);
    assertTrue(result, "Game creation should succeed");
    assertNotNull(app.getBoardGame(), "BoardGame should be created");

    // Test invalid game creation
    boolean invalidResult = app.createGame(-1, 1, 5);
    assertFalse(invalidResult, "Game creation with invalid params should fail");
    assertTrue(errContent.toString().contains("Failed to create game"),
        "Error message should be displayed");
  }

  @Test
  public void testAddPlayer() throws Exception {
    // Setup
    app.createGame(1, 2, 10);

    // Test adding player successfully
    boolean result = app.addPlayer("TestPlayer");
    assertTrue(result, "Adding player should succeed");

    // Verify player was added
    BoardGame game = app.getBoardGame();
    Field playersField = BoardGame.class.getDeclaredField("players");
    playersField.setAccessible(true);
    @SuppressWarnings("unchecked")
    java.util.Map<Player, Integer> players = (java.util.Map<Player, Integer>) playersField.get(game);

    boolean playerFound = false;
    for (Player p : players.keySet()) {
      if (p.getName().equals("TestPlayer")) {
        playerFound = true;
        break;
      }
    }
    assertTrue(playerFound, "Player should be added to the game");

    // Test adding player when game not created
    app = new BoardGameApp();
    boolean noGameResult = app.addPlayer("AnotherPlayer");
    assertFalse(noGameResult, "Adding player without game should fail");
    assertTrue(errContent.toString().contains("Game not created yet"),
        "Error message should indicate game not created");
  }

  @Test
  public void testStartGame() throws Exception {
    // Setup with mock board game
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp mockApp = new BoardGameApp(mockUi);

    // Inject mock board game using reflection
    MockBoardGame mockGame = new MockBoardGame(1, 2, 10, mockUi);
    Field boardGameField = BoardGameApp.class.getDeclaredField("boardGame");
    boardGameField.setAccessible(true);
    boardGameField.set(mockApp, mockGame);

    // Test successful start
    boolean result = mockApp.startGame();
    assertTrue(result, "Starting game should succeed");
    assertTrue(mockGame.wasInitializeGameCalled(), "initialiseGame should be called");

    // Test exception during start
    mockGame.setThrowExceptionOnInit(true);
    boolean exceptionResult = mockApp.startGame();
    assertFalse(exceptionResult, "Start should fail when exception occurs");

    // Test start without game
    BoardGameApp emptyApp = new BoardGameApp();
    boolean noGameResult = emptyApp.startGame();
    assertFalse(noGameResult, "Starting without game should fail");
  }

  @Test
  public void testPlayFullGame() throws Exception {
    // Setup with mock board game
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp mockApp = new BoardGameApp(mockUi);

    // Inject mock board game
    MockBoardGame mockGame = new MockBoardGame(1, 2, 10, mockUi);
    Field boardGameField = BoardGameApp.class.getDeclaredField("boardGame");
    boardGameField.setAccessible(true);
    boardGameField.set(mockApp, mockGame);

    // Test successful play
    boolean result = mockApp.playFullGame();
    assertTrue(result, "Playing game should succeed");
    assertTrue(mockGame.wasPlayGameCalled(), "playGame should be called");

    // Test exception during play
    mockGame.setThrowExceptionOnPlay(true);
    boolean exceptionResult = mockApp.playFullGame();
    assertFalse(exceptionResult, "Play should fail when exception occurs");

    // Test play without game
    BoardGameApp emptyApp = new BoardGameApp();
    boolean noGameResult = emptyApp.playFullGame();
    assertFalse(noGameResult, "Playing without game should fail");
  }

  @Test
  public void testPlayTurn() throws Exception {
    // Setup with mock board game
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp mockApp = new BoardGameApp(mockUi);

    // Inject mock board game
    MockBoardGame mockGame = new MockBoardGame(1, 2, 10, mockUi);
    Field boardGameField = BoardGameApp.class.getDeclaredField("boardGame");
    boardGameField.setAccessible(true);
    boardGameField.set(mockApp, mockGame);

    // Test successful turn
    boolean result = mockApp.playTurn();
    assertTrue(result, "Playing turn should succeed");
    assertTrue(mockGame.wasPlayCurrentPlayerCalled(), "playCurrentPlayer should be called");

    // Test exception during turn
    mockGame.setThrowExceptionOnPlayCurrent(true);
    boolean exceptionResult = mockApp.playTurn();
    assertFalse(exceptionResult, "Turn should fail when exception occurs");

    // Test turn without game
    BoardGameApp emptyApp = new BoardGameApp();
    boolean noGameResult = emptyApp.playTurn();
    assertFalse(noGameResult, "Playing turn without game should fail");
  }

  @Test
  public void testSetupAndPlayGame() throws Exception {
    // Use a mock UI to verify behavior
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp mockApp = new BoardGameApp(mockUi);

    // Setup test data
    String[] playerNames = {"Player1", "Player2"};

    // Test successful setup and play
    boolean result = mockApp.setupAndPlayGame(1, 10, playerNames);
    assertTrue(result, "Setup and play should succeed");

    // Verify game was created with correct parameters
    BoardGame game = mockApp.getBoardGame();
    assertNotNull(game, "Game should be created");

    // Verify players were added
    Field playersField = BoardGame.class.getDeclaredField("players");
    playersField.setAccessible(true);
    @SuppressWarnings("unchecked")
    java.util.Map<Player, Integer> players = (java.util.Map<Player, Integer>) playersField.get(game);

    assertEquals(2, players.size(), "Both players should be added");

    boolean player1Found = false;
    boolean player2Found = false;
    for (Player p : players.keySet()) {
      if (p.getName().equals("Player1")) player1Found = true;
      if (p.getName().equals("Player2")) player2Found = true;
    }
    assertTrue(player1Found && player2Found, "Both players should be in the game");

    // Verify output contains setup info
    String output = outContent.toString();
    assertTrue(output.contains("Board Size: 10"), "Output should show board size");
    assertTrue(output.contains("Number of Dice: 1"), "Output should show dice count");
    assertTrue(output.contains("1. Player1"), "Output should list Player1");
    assertTrue(output.contains("2. Player2"), "Output should list Player2");
  }

  @Test
  public void testIsGameActive() throws Exception {
    // Setup with mock board game
    MockGameUI mockUi = new MockGameUI();
    BoardGameApp mockApp = new BoardGameApp(mockUi);

    // Without game
    assertFalse(mockApp.isGameActive(), "Game should not be active initially");

    // With inactive game
    MockBoardGame mockGame = new MockBoardGame(1, 2, 10, mockUi);
    Field boardGameField = BoardGameApp.class.getDeclaredField("boardGame");
    boardGameField.setAccessible(true);
    boardGameField.set(mockApp, mockGame);

    // Set playing field to false
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    playingField.set(mockGame, false);

    assertFalse(mockApp.isGameActive(), "Game should not be active");

    // With active game
    playingField.set(mockGame, true);
    assertTrue(mockApp.isGameActive(), "Game should be active");
  }

  @Test
  public void testDisplayError() throws Exception {
    // Test private displayError method using reflection
    java.lang.reflect.Method displayErrorMethod =
        BoardGameApp.class.getDeclaredMethod("displayError", String.class);
    displayErrorMethod.setAccessible(true);

    displayErrorMethod.invoke(app, "Test error message");

    String errorOutput = errContent.toString();
    assertTrue(errorOutput.contains("ERROR: Test error message"),
        "Error message should be displayed correctly");
  }

}
