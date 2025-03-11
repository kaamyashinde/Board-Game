

import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.dice.Dice;
import edu.ntnu.iir.bidata.model.tile.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

/**
 * @author Durva
 */

public class BoardGameTest {

  private Board board;
  private ArrayList<Player> players;
  private TestDice dice;
  private BoardGame game;
  private Player player;

  /**
   * A simple subclass of Dice that always returns a fixed roll value.
   */
  class TestDice extends Dice {
    private final int fixedValue;

    public TestDice(int numberOfDice, int fixedValue) {
      super(numberOfDice);
      this.fixedValue = fixedValue;
    }

    @Override
    public void rollAllDice() {
      // Bypass random behavior.
    }

    @Override
    public int sumOfRolledValues() {
      return fixedValue;
    }
  }

  @BeforeEach
  public void setup() {
    // Create a board with 10 tiles (indexed 0 to 9)
    board = new Board(10);
    players = new ArrayList<>();
    player = new Player("TestPlayer");
    // Set the player's starting tile to the tile with id 0.
    Tile startingTile = board.getTiles().get(0);
    player.placeOnTile(startingTile);
    players.add(player);

    // Use TestDice that always returns a fixed value of 3.
    dice = new TestDice(1, 3);
    // Since BoardGame expects four arguments, pass the current player as the fourth argument.
    game = new BoardGame(board, players, dice, player);
  }

  @Test
  public void testPlayMovesPlayer() {
    // Given the dice always returns 3, when play() is called,
    // the player should move from tile 0 to tile 3.
    game.play();
    assertEquals(3, player.getCurrentTile().getId(),
        "Player should move 3 steps from tile 0 to tile 3");
  }
}
