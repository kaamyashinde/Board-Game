package edu.ntnu.iir.bidata.model.tile.core;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TileTest {

  private Tile tile;
  private Tile nextTile;
  private Player player;
  private TileAction mockAction;

  @BeforeEach
  void setUp() {
    tile = new Tile(1);
    nextTile = new Tile(2);
    player = mock(Player.class);
    mockAction = mock(TileAction.class);
  }

  @Test
  void testConstructorWithValidId() {
    assertDoesNotThrow(() -> new Tile(1));
    assertEquals(1, tile.getId());
    assertNull(tile.getAction());
  }

  @Test
  void testConstructorWithAction() {
    Tile tileWithAction = new Tile(1, mockAction);
    assertEquals(1, tileWithAction.getId());
    assertEquals(mockAction, tileWithAction.getAction());
  }

  @Test
  void testConstructorWithNegativeId() {
    assertThrows(IllegalArgumentException.class, () -> new Tile(-1));
  }

  @Test
  void testSetNextTile() {
    tile.setNextTile(nextTile);
    assertEquals(nextTile, tile.getNextTile());
    tile.setNextTile(null);
    assertNull(tile.getNextTile());
  }

  @Test
  void testIsFirstTile() {
    Tile firstTile = new Tile(0);
    assertTrue(firstTile.isFirstTile());
    assertFalse(tile.isFirstTile());
  }

  @Test
  void testGetNextTileWithSteps() {
    Tile tile1 = new Tile(1);
    Tile tile2 = new Tile(2);
    Tile tile3 = new Tile(3);

    tile1.setNextTile(tile2);
    tile2.setNextTile(tile3);

    assertEquals(tile2, tile1.getNextTile(1));
    assertEquals(tile3, tile1.getNextTile(2));
  }

  @Test
  void testGetNextTileWithStepsThrowsException() {
    Tile tile1 = new Tile(1);
    Tile tile2 = new Tile(2);

    tile1.setNextTile(tile2);

    assertThrows(GameException.class, () -> tile1.getNextTile(2));
  }

  @Test
  void testIsLastTile() {
    assertTrue(tile.isLastTile());
    tile.setNextTile(nextTile);
    assertFalse(tile.isLastTile());
  }

  @Test
  void testEquals() {
    Tile sameTile = new Tile(1);
    Tile differentTile = new Tile(2);

    assertTrue(tile.equals(sameTile));
    assertFalse(tile.equals(differentTile));
    assertFalse(tile.equals(null));
    assertFalse(tile.equals(new Object()));
  }

  @Test
  void testHashCode() {
    Tile sameTile = new Tile(1);
    assertEquals(tile.hashCode(), sameTile.hashCode());
  }
} 