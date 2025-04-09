package edu.ntnu.iir.bidata.model;


import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.board.BoardGame;
import edu.ntnu.iir.bidata.model.board.BoardGameObserver;
import edu.ntnu.iir.bidata.ui.ConsoleGameUI;
import edu.ntnu.iir.bidata.ui.GameUI;
import edu.ntnu.iir.bidata.model.dice.Dice;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author Durva (Updated version)
 */

public class BoardGameTest {

  /**
   * A TestDice subclass that always returns a fixed roll value.
   */
  class TestDice extends Dice {
    private final int fixedValue;

    public TestDice(int numberOfDice, int fixedValue) {
      super(numberOfDice);
      this.fixedValue = fixedValue;
    }

    @Override
    public void rollAllDice() {
      // Do nothing to bypass randomness.
    }

    @Override
    public int sumOfRolledValues() {
      return fixedValue;
    }
  }

  /**
   * Test observer implementation to verify observer pattern
   */
  class TestObserver implements BoardGameObserver {
    private Player lastMovedPlayer;
    private int lastPosition;
    private Player lastWinner;
    private Player lastTurnPlayer;

    @Override
    public void onPlayerMoved(Player player, int newPosition) {
      this.lastMovedPlayer = player;
      this.lastPosition = newPosition;
    }

    @Override
    public void onGameWon(Player winner) {
      this.lastWinner = winner;
    }

    @Override
    public void onTurnChanged(Player currentPlayer) {
      this.lastTurnPlayer = currentPlayer;
    }

    // Getter methods for test verification
    public Player getLastMovedPlayer() {
      return lastMovedPlayer;
    }

    public int getLastPosition() {
      return lastPosition;
    }

    public Player getLastWinner() {
      return lastWinner;
    }

    public Player getLastTurnPlayer() {
      return lastTurnPlayer;
    }
  }

  private BoardGame game;
  private Player player;
  private GameUI ui;
  private TestObserver observer;

  @BeforeEach
  public void setup() throws Exception {
    // Create a UI implementation for the game
    ui = new ConsoleGameUI();

    // Create a game with 1 dice, 2 players (minimum required), and a board of size 10.
    game = new BoardGame(1, 2, 10, ui);

    // Create and add the test player
    player = new Player("TestPlayer");
    game.addPlayer(player);

    // Add a second player (since the game now requires minimum 2 players)
    Player secondPlayer = new Player("SecondPlayer");
    game.addPlayer(secondPlayer);

    // Add test observer
    observer = new TestObserver();
    game.addObserver(observer);

    // Initialize the game
    game.initialiseGame();

    TestDice testDice = new TestDice(1, 3);
    Field diceField = BoardGame.class.getDeclaredField("dice");
    diceField.setAccessible(true);
    diceField.set(game, testDice);
  }


  @Test
  public void testAddPlayer() throws Exception {
    // Create a new game for this test to isolate the player adding logic
    BoardGame newGame = new BoardGame(1, 2, 10, ui);
    Player newPlayer = new Player("NewPlayer");

    // Add the player
    newGame.addPlayer(newPlayer);

    // When a player is added, their current tile is set to the board's tile 0.
    assertEquals(0, newPlayer.getCurrentTile().getId(), "Player should start at tile 0");

    // Also, the players HashMap should contain the player
    Field playersField = BoardGame.class.getDeclaredField("players");
    playersField.setAccessible(true);
    @SuppressWarnings("unchecked")
    Map<Player, Integer> playersMap = (Map<Player, Integer>) playersField.get(newGame);
    assertTrue(playersMap.containsKey(newPlayer));
    assertEquals(0, (int) playersMap.get(newPlayer));
  }


  @Test
  public void testInitialiseGame() throws Exception {
    // initialiseGame should set the currentPlayer to the first player in the players map.
    Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
    currentPlayerField.setAccessible(true);
    Player cp = (Player) currentPlayerField.get(game);
    assertEquals(player, cp, "Current player should be the one added");

    // Verify playing flag is set to true
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertTrue((boolean) playingField.get(game), "Game should be marked as playing");


    // Verify that the observer was notified of turn change
    assertNotNull(observer.getLastTurnPlayer(), "Observer should be notified of turn change");
  }

  @Test
  public void testPlayCurrentPlayerNonWinning() throws Exception {

    // Set the current player to our test player
    Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
    currentPlayerField.setAccessible(true);
    currentPlayerField.set(game, player);

    // Retrieve the board and ensure the player starts on tile 0
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b = (Board) boardField.get(game);
    player.setCurrentTile(b.getTiles().get(0));

    // Call playCurrentPlayer; expecting player to move 3 steps forward
    game.playCurrentPlayer();
    assertEquals(3, player.getCurrentTile().getId(),
        "Player should move 3 steps from tile 0 to tile 3");

    // Ensure that playing remains true (non-winning move should not end the game)
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertTrue((boolean) playingField.get(game), "Game should still be playing");

    // Verify observer was notified of player movement
    assertEquals(player, observer.getLastMovedPlayer(), "Observer should be notified of player movement");
    assertEquals(3, observer.getLastPosition(), "Observer should receive correct new position");
  }


  @Test
  public void testPlayCurrentPlayerWinning() throws Exception {
    // Set the current player to our test player
    Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
    currentPlayerField.setAccessible(true);
    currentPlayerField.set(game, player);

    // Get the board
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b = (Board) boardField.get(game);

    // Set player's tile to the last tile that would lead to a win with a roll of 3
    // If board size is 10, the last tile is at index 9, so set to index 7
    player.setCurrentTile(b.getTiles().get(7));

    // Call playCurrentPlayer; expecting the winning path to execute
    game.playCurrentPlayer();

    // The game should have ended
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertFalse((boolean) playingField.get(game), "Game should stop playing after a win");

    // Verify observer was notified of game win
    assertEquals(player, observer.getLastWinner(), "Observer should be notified of game win");

  }

  @Test
  public void testPlayGame() throws Exception {
    // Create a new game with two players.
    BoardGame game2 = new BoardGame(1, 2, 10, ui);
    Player p1 = new Player("P1");
    Player p2 = new Player("P2");
    TestObserver observer2 = new TestObserver();

    game2.addObserver(observer2);
    game2.addPlayer(p1);
    game2.addPlayer(p2);
    game2.initialiseGame();

    // Replace dice in game2 with TestDice returning 3.
    TestDice testDice = new TestDice(1, 3);
    Field diceField = BoardGame.class.getDeclaredField("dice");
    diceField.setAccessible(true);
    diceField.set(game2, testDice);

    // Access the board.
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b2 = (Board) boardField.get(game2);

    // Force p1 to be the current player
    Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
    currentPlayerField.setAccessible(true);
    currentPlayerField.set(game2, p1);

    // Set up p1 so that p1 wins in one move: set to tile 7 (7+3=10 which is the end)
    p1.setCurrentTile(b2.getTiles().get(7));

    // Set p2 to start at 0
    p2.setCurrentTile(b2.getTiles().get(0));

    // Call playGame. should end when p1 wins.
    game2.playGame();

    // Verify that game2's playing flag is false.
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertFalse((boolean) playingField.get(game2), "Game should have ended after a win");

    // Verify that observer was notified of game win
    assertEquals(p1, observer2.getLastWinner(), "Observer should be notified that p1 won");
  }

  @Test
  public void testObserverPattern() {
    // Create a new observer for this test
    TestObserver testObserver = new TestObserver();
    game.addObserver(testObserver);

    // Set current player to our test player
    try {
      Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
      currentPlayerField.setAccessible(true);
      currentPlayerField.set(game, player);
    } catch (Exception e) {
      fail("Failed to set current player: " + e.getMessage());
    }

    // Trigger turn changed notification
    game.notifyTurnChanged(player);
    assertEquals(player, testObserver.getLastTurnPlayer(), "Observer should be notified of turn change");

    // Remove the observer
    game.removeObserver(testObserver);

    // Clear the observer's state
    testObserver = new TestObserver();

    // Trigger notification again - observer should not be updated
    game.notifyTurnChanged(player);
    assertNull(testObserver.getLastTurnPlayer(), "Removed observer should not receive notifications");
  }

  // Reflection helper method to call private methods for testing
  @SuppressWarnings("unchecked")
  private void callPrivateMethod(Object instance, String methodName, Object... args) throws Exception {
    Class<?>[] paramTypes = new Class<?>[args.length];
    for (int i = 0; i < args.length; i++) {
      paramTypes[i] = args[i].getClass();
    }

    java.lang.reflect.Method method = instance.getClass().getDeclaredMethod(methodName, paramTypes);
    method.setAccessible(true);
    method.invoke(instance, args);
  }
}
