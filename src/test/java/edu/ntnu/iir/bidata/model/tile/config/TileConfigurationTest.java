package edu.ntnu.iir.bidata.model.tile.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TileConfigurationTest {

    private TileConfiguration tileConfig;
    private TileConfiguration easyTileConfig;
    private TileConfiguration hardTileConfig;

    @BeforeEach
    void setUp() {
        tileConfig = new TileConfiguration(); // Default is medium
        easyTileConfig = new TileConfiguration("easy");
        hardTileConfig = new TileConfiguration("hard");
    }

    @Test
    void isLadderStart_WithValidLadderStart_ShouldReturnTrue() {
        // Medium difficulty (default)
        assertTrue(tileConfig.isLadderStart(3));
        assertTrue(tileConfig.isLadderStart(8));
        assertTrue(tileConfig.isLadderStart(14));
        assertTrue(tileConfig.isLadderStart(31));
        assertTrue(tileConfig.isLadderStart(59));
        assertTrue(tileConfig.isLadderStart(83));
        assertTrue(tileConfig.isLadderStart(90));
    }

    @Test
    void isLadderStart_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isLadderStart(1));
        assertFalse(tileConfig.isLadderStart(10));
        assertFalse(tileConfig.isLadderStart(30));
    }

    @Test
    void getLadderEnd_WithValidLadderStart_ShouldReturnCorrectEnd() {
        // Medium difficulty (default)
        assertEquals(36, tileConfig.getLadderEnd(3));
        assertEquals(12, tileConfig.getLadderEnd(8));
        assertEquals(26, tileConfig.getLadderEnd(14));
        assertEquals(73, tileConfig.getLadderEnd(31));
        assertEquals(80, tileConfig.getLadderEnd(59));
        assertEquals(97, tileConfig.getLadderEnd(83));
        assertEquals(92, tileConfig.getLadderEnd(90));
    }

    @Test
    void isSnakeHead_WithValidSnakeHead_ShouldReturnTrue() {
        // Medium difficulty (default)
        assertTrue(tileConfig.isSnakeHead(38));
        assertTrue(tileConfig.isSnakeHead(29));
        assertTrue(tileConfig.isSnakeHead(78));
        assertTrue(tileConfig.isSnakeHead(89));
        assertTrue(tileConfig.isSnakeHead(95));
        assertTrue(tileConfig.isSnakeHead(99));
    }

    @Test
    void isSnakeHead_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isSnakeHead(1));
        assertFalse(tileConfig.isSnakeHead(10));
        assertFalse(tileConfig.isSnakeHead(30));
    }

    @Test
    void getSnakeTail_WithValidSnakeHead_ShouldReturnCorrectTail() {
        // Medium difficulty (default)
        assertEquals(2, tileConfig.getSnakeTail(38));
        assertEquals(11, tileConfig.getSnakeTail(29));
        assertEquals(15, tileConfig.getSnakeTail(78));
        assertEquals(86, tileConfig.getSnakeTail(89));
        assertEquals(75, tileConfig.getSnakeTail(95));
        assertEquals(41, tileConfig.getSnakeTail(99));
    }

    @Test
    void isSkipTurn_WithValidSkipTurnPosition_ShouldReturnTrue() {
        assertTrue(tileConfig.isSkipTurn(5));
    }

    @Test
    void isSkipTurn_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isSkipTurn(1));
        assertFalse(tileConfig.isSkipTurn(10));
        assertFalse(tileConfig.isSkipTurn(30));
    }

    @Test
    void isMoveBack_WithValidMoveBackPosition_ShouldReturnTrue() {
        assertTrue(tileConfig.isMoveBack(10));
        assertTrue(tileConfig.isMoveBack(20));
    }

    @Test
    void isMoveBack_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isMoveBack(1));
        assertFalse(tileConfig.isMoveBack(15));
        assertFalse(tileConfig.isMoveBack(30));
    }

    @Test
    void getMoveBackSteps_WithValidMoveBackPosition_ShouldReturnCorrectSteps() {
        assertEquals(3, tileConfig.getMoveBackSteps(10));
        assertEquals(2, tileConfig.getMoveBackSteps(20));
    }

    @Test
    void isSwitchPlaces_WithValidSwitchPlacesPosition_ShouldReturnTrue() {
        assertTrue(tileConfig.isSwitchPlaces(12));
    }

    @Test
    void isSwitchPlaces_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isSwitchPlaces(1));
        assertFalse(tileConfig.isSwitchPlaces(10));
        assertFalse(tileConfig.isSwitchPlaces(30));
    }

    @Test
    void easyLevel_ShouldHaveCorrectLadderConfiguration() {
        // Test a few key ladders in easy configuration
        assertTrue(easyTileConfig.isLadderStart(4));
        assertTrue(easyTileConfig.isLadderStart(19));
        assertTrue(easyTileConfig.isLadderStart(57));

        assertEquals(16, easyTileConfig.getLadderEnd(4));
        assertEquals(40, easyTileConfig.getLadderEnd(19));
        assertEquals(85, easyTileConfig.getLadderEnd(57));
    }

    @Test
    void easyLevel_ShouldHaveCorrectSnakeConfiguration() {
        // Test a few key snakes in easy configuration
        assertTrue(easyTileConfig.isSnakeHead(49));
        assertTrue(easyTileConfig.isSnakeHead(88));

        assertEquals(31, easyTileConfig.getSnakeTail(49));
        assertEquals(45, easyTileConfig.getSnakeTail(88));
    }

    @Test
    void easyLevel_ShouldNotHaveSpecialTiles() {
        // Easy level should not have special tiles
        assertFalse(easyTileConfig.isSkipTurn(5));
        assertFalse(easyTileConfig.isMoveBack(10));
        assertFalse(easyTileConfig.isSwitchPlaces(12));
    }

    @Test
    void hardLevel_ShouldHaveCorrectLadderConfiguration() {
        // Test a few key ladders in hard configuration
        assertTrue(hardTileConfig.isLadderStart(4));
        assertTrue(hardTileConfig.isLadderStart(42));
        assertTrue(hardTileConfig.isLadderStart(72));

        assertEquals(25, hardTileConfig.getLadderEnd(4));
        assertEquals(84, hardTileConfig.getLadderEnd(42));
        assertEquals(88, hardTileConfig.getLadderEnd(72));
    }

    @Test
    void hardLevel_ShouldHaveCorrectSnakeConfiguration() {
        // Test a few key snakes in hard configuration
        assertTrue(hardTileConfig.isSnakeHead(35));
        assertTrue(hardTileConfig.isSnakeHead(57));
        assertTrue(hardTileConfig.isSnakeHead(99));

        assertEquals(7, hardTileConfig.getSnakeTail(35));
        assertEquals(3, hardTileConfig.getSnakeTail(57));
        assertEquals(83, hardTileConfig.getSnakeTail(99));
    }

    @Test
    void hardLevel_ShouldNotHaveSpecialTiles() {
        // Hard level should not have special tiles
        assertFalse(hardTileConfig.isSkipTurn(5));
        assertFalse(hardTileConfig.isMoveBack(10));
        assertFalse(hardTileConfig.isSwitchPlaces(12));
    }
}