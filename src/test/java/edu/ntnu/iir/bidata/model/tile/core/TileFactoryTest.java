package edu.ntnu.iir.bidata.model.tile.core;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.config.TileConfiguration;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.actions.snakeandladder.SnakeAction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TileFactoryTest {

    @Mock
    private Player mockPlayer1;
    @Mock
    private Player mockPlayer2;
    @Mock
    private TileConfiguration mockTileConfig;

    private TileFactory tileFactory;
    private List<Player> players;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        players = Arrays.asList(mockPlayer1, mockPlayer2);
        tileFactory = new TileFactory(players, mockTileConfig);
    }

    @Test
    void createTile_WithLadderStart_ShouldCreateTileWithLadderAction() {
        // Arrange
        int position = 3;
        int ladderEnd = 12;
        when(mockTileConfig.isLadderStart(position)).thenReturn(true);
        when(mockTileConfig.getLadderEnd(position)).thenReturn(ladderEnd);

        // Act
        Tile tile = tileFactory.createTile(position);

        // Assert
        assertNotNull(tile);
        assertTrue(tile.getAction() instanceof LadderAction);
        LadderAction ladderAction = (LadderAction) tile.getAction();
        assertEquals(ladderEnd, ladderAction.getTopTileId());
    }

    @Test
    void createTile_WithSnakeHead_ShouldCreateTileWithSnakeAction() {
        // Arrange
        int position = 8;
        int snakeTail = 4;
        when(mockTileConfig.isSnakeHead(position)).thenReturn(true);
        when(mockTileConfig.getSnakeTail(position)).thenReturn(snakeTail);

        // Act
        Tile tile = tileFactory.createTile(position);

        // Assert
        assertNotNull(tile);
        assertTrue(tile.getAction() instanceof SnakeAction);
        SnakeAction snakeAction = (SnakeAction) tile.getAction();
        assertEquals(snakeTail, snakeAction.getTailTileId());
    }

    @Test
    void createTile_WithNormalPosition_ShouldCreateTileWithoutAction() {
        // Arrange
        int position = 1;
        when(mockTileConfig.isLadderStart(position)).thenReturn(false);
        when(mockTileConfig.isSnakeHead(position)).thenReturn(false);

        // Act
        Tile tile = tileFactory.createTile(position);

        // Assert
        assertNotNull(tile);
        assertNull(tile.getAction());
    }

    @Test
    void createTile_ShouldSetCorrectPosition() {
        // Arrange
        int position = 5;
        when(mockTileConfig.isLadderStart(position)).thenReturn(false);
        when(mockTileConfig.isSnakeHead(position)).thenReturn(false);

        // Act
        Tile tile = tileFactory.createTile(position);

        // Assert
        assertEquals(position, tile.getId());
    }
} 