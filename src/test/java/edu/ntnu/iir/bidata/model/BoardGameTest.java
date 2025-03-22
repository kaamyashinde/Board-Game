package edu.ntnu.iir.bidata.model;


import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.tile.Tile;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.util.HashMap;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author Durva
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

  private BoardGame game;
  private Player player;

  @BeforeEach
  public void setup() throws Exception {
    // Create a game with 1 dice, 1 player, and a board of size 10.
    game = new BoardGame(1, 1, 10);
    player = new Player("TestPlayer");
    game.addPlayer(player);
    game.initialiseGame();

    // Replace the internally created dice with our TestDice returning 3.
    TestDice testDice = new TestDice(1, 3);
    Field diceField = BoardGame.class.getDeclaredField("dice");
    diceField.setAccessible(true);
    diceField.set(game, testDice);
  }

  @Test
  public void testAddPlayer() throws Exception {
    // When a player is added, their current tile is set to the board's tile 0.
    // Access the board via reflection.
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b = (Board) boardField.get(game);
    assertEquals(0, player.getCurrentTile().getId(), "Player should start at tile 0");
    // Also, the players HashMap should contain the player with score 0.
    Field playersField = BoardGame.class.getDeclaredField("players");
    playersField.setAccessible(true);
    @SuppressWarnings("unchecked")
    HashMap<Player, Integer> playersMap = (HashMap<Player, Integer>) playersField.get(game);
    assertTrue(playersMap.containsKey(player));
    assertEquals(0, (int) playersMap.get(player));
  }

  @Test
  public void testInitialiseGame() throws Exception {
    // initialiseGame should set the currentPlayer to the first player in the players map.
    Field currentPlayerField = BoardGame.class.getDeclaredField("currentPlayer");
    currentPlayerField.setAccessible(true);
    Player cp = (Player) currentPlayerField.get(game);
    assertEquals(player, cp, "Current player should be the one added");
  }

  @Test
  public void testPlayCurrentPlayerNonWinning() throws Exception {
    // Retrieve the board and ensure the player starts on tile 0.
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b = (Board) boardField.get(game);
    player.setCurrentTile(b.getTiles().get(0));

    // Manually set 'playing' to true since playGame() wasn't called.
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    playingField.set(game, true);

    // Call playCurrentPlayer; expecting the else branch to update player's tile.
    game.playCurrentPlayer();
    assertEquals(3, player.getCurrentTile().getId(),
        "Player should move 3 steps from tile 0 to tile 3");

    // Ensure that playing remains true (non-winning move should not end the game).
    assertTrue((boolean) playingField.get(game), "Game should still be playing");
  }


  @Test
  public void testPlayCurrentPlayerWinning() throws Exception {
    // Test the winning branch:
    // Set player's tile to 8 so that 8+3=11 >= board size (10).
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b = (Board) boardField.get(game);
    player.setCurrentTile(b.getTiles().get(8));

    // Call playCurrentPlayer; expecting the winning branch to execute.
    game.playCurrentPlayer();
    // The winning branch prints messages and sets playing to false.
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertFalse((boolean) playingField.get(game), "Game should stop playing after a win");

    // In the winning branch, the player's tile is not updated (remains as it was).
    assertEquals(8, player.getCurrentTile().getId(), "Player's tile remains unchanged in winning branch");
  }

  @Test
  public void testPlayGame() throws Exception {
    // Create a new game with two players.
    BoardGame game2 = new BoardGame(1, 2, 10);
    Player p1 = new Player("P1");
    Player p2 = new Player("P2");
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

    // Set up p1 so that p1 wins in one move: set its current tile to 8 (8+3=11 >= 10).
    p1.setCurrentTile(b2.getTiles().get(8));
    // Set p2 to start at 0.
    p2.setCurrentTile(b2.getTiles().get(0));

    // Call playGame. The for-loop should run; when p1 wins, playing becomes false and breaks the loop.
    game2.playGame();

    // Verify that game2's playing flag is false.
    Field playingField = BoardGame.class.getDeclaredField("playing");
    playingField.setAccessible(true);
    assertFalse((boolean) playingField.get(game2), "Game should have ended after a win");

    // Verify that p1's score (in the players map) is updated.
    Field playersField = BoardGame.class.getDeclaredField("players");
    playersField.setAccessible(true);
    @SuppressWarnings("unchecked")
    HashMap<Player, Integer> playersMap = (HashMap<Player, Integer>) playersField.get(game2);
    // In the winning branch, p1's current tile remains unchanged (tile id 8).
    assertEquals(8, (int) playersMap.get(p1), "P1's score should be updated to its tile id (8)");
    // p2 was not played.
    assertEquals(0, (int) playersMap.get(p2), "P2's score should remain 0");
  }

  @Test
  public void testGetWinner() throws Exception {
    // Test the getWinner method.
    // Create a new game instance and set its playerArrayList.
    BoardGame game3 = new BoardGame(1, 1, 10);
    Player p = new Player("Winner");
    // Access the board.
    Field boardField = BoardGame.class.getDeclaredField("board");
    boardField.setAccessible(true);
    Board b3 = (Board) boardField.get(game3);
    int lastTileId = b3.getTiles().size() - 1;
    // Set player's tile to the last tile.
    p.setCurrentTile(b3.getTiles().get(lastTileId));

    // Manually set playerArrayList to contain this player.
    ArrayList<Player> list = new ArrayList<>();
    list.add(p);
    Field playerArrayListField = BoardGame.class.getDeclaredField("playerArrayList");
    playerArrayListField.setAccessible(true);
    playerArrayListField.set(game3, list);

    // Capture System.out output.
    ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    PrintStream originalOut = System.out;
    System.setOut(new PrintStream(outContent));

    game3.getWinner();

    System.setOut(originalOut);
    // Verify that the output contains the winner's name.
    assertTrue(outContent.toString().contains("Winner"), "Output should announce the winner");
  }
}
