package edu.ntnu.iir.bidata.model.player;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class PlayerTest {

  private Player player;
  private Tile mockTile;
  private Tile mockNextTile;

  @BeforeEach
  void setUp() {
    player = new Player("TestPlayer");
    mockTile = mock(Tile.class);
    mockNextTile = mock(Tile.class);
    player.setCurrentTile(mockTile);
  }

  @Test
  void testConstructor() {
    Player newPlayer = new Player("NewPlayer");
    assertEquals("NewPlayer", newPlayer.getName());
    assertNull(newPlayer.getCurrentTile());
  }

  @Test
  void testConstructorWithTokenImage() {
    Player playerWithToken = new Player("TokenPlayer", "token_blue.png");
    assertEquals("TokenPlayer", playerWithToken.getName());
    assertEquals("token_blue.png", playerWithToken.getTokenImage());
  }

  @Test
  void testMove() {
    when(mockTile.getNextTile(3)).thenReturn(mockNextTile);
    player.move(3);
    verify(mockTile).getNextTile(3);
    assertEquals(mockNextTile, player.getCurrentTile());
  }

  @Test
  void testIsOnFirstTile() {
    when(mockTile.isFirstTile()).thenReturn(true);
    assertTrue(player.isOnFirstTile());
    verify(mockTile).isFirstTile();

    when(mockTile.isFirstTile()).thenReturn(false);
    assertFalse(player.isOnFirstTile());
  }

  @Test
  void testIsOnLastTile() {
    when(mockTile.isLastTile()).thenReturn(true);
    assertTrue(player.isOnLastTile());
    verify(mockTile).isLastTile();

    when(mockTile.isLastTile()).thenReturn(false);
    assertFalse(player.isOnLastTile());
  }

  @Test
  void testGetCurrentPosition() {
    when(mockTile.getId()).thenReturn(5);
    assertEquals(5, player.getCurrentPosition());
    verify(mockTile).getId();

    // Test when currentTile is null
    Player playerWithoutTile = new Player("NoTilePlayer");
    assertEquals(-1, playerWithoutTile.getCurrentPosition());
  }

  @Test
  void testGettersAndSetters() {
    // Test name getter and setter
    player.setName("NewName");
    assertEquals("NewName", player.getName());

    // Test currentTile getter and setter
    Tile newTile = mock(Tile.class);
    player.setCurrentTile(newTile);
    assertEquals(newTile, player.getCurrentTile());
  }

  @Test
  void testTokenImageGetterAndSetter() {
    player.setTokenImage("token_red.png");
    assertEquals("token_red.png", player.getTokenImage());
  }

  @Test
  void testEqualsAndHashCodeWithTokenImage() {
    Player p1 = new Player("A", "token_green.png");
    Player p2 = new Player("A", "token_green.png");
    Player p3 = new Player("A", "token_yellow.png");
    assertEquals(p1, p2);
    assertEquals(p1.hashCode(), p2.hashCode());
    assertNotEquals(p1, p3);
    assertNotEquals(p1.hashCode(), p3.hashCode());
  }
} 