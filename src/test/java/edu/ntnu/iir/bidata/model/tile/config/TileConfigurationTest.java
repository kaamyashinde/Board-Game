package edu.ntnu.iir.bidata.model.tile.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class TileConfigurationTest {

    private TileConfiguration tileConfig;

    @BeforeEach
    void setUp() {
        tileConfig = new TileConfiguration();
    }

    @Test
    void isLadderStart_WithValidLadderStart_ShouldReturnTrue() {
        assertTrue(tileConfig.isLadderStart(3));
        assertTrue(tileConfig.isLadderStart(15));
        assertTrue(tileConfig.isLadderStart(22));
    }

    @Test
    void isLadderStart_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isLadderStart(1));
        assertFalse(tileConfig.isLadderStart(10));
        assertFalse(tileConfig.isLadderStart(30));
    }

    @Test
    void getLadderEnd_WithValidLadderStart_ShouldReturnCorrectEnd() {
        assertEquals(12, tileConfig.getLadderEnd(3));
        assertEquals(22, tileConfig.getLadderEnd(15));
        assertEquals(25, tileConfig.getLadderEnd(22));
    }

    @Test
    void isSnakeHead_WithValidSnakeHead_ShouldReturnTrue() {
        assertTrue(tileConfig.isSnakeHead(8));
        assertTrue(tileConfig.isSnakeHead(18));
        assertTrue(tileConfig.isSnakeHead(24));
    }

    @Test
    void isSnakeHead_WithInvalidPosition_ShouldReturnFalse() {
        assertFalse(tileConfig.isSnakeHead(1));
        assertFalse(tileConfig.isSnakeHead(10));
        assertFalse(tileConfig.isSnakeHead(30));
    }

    @Test
    void getSnakeTail_WithValidSnakeHead_ShouldReturnCorrectTail() {
        assertEquals(4, tileConfig.getSnakeTail(8));
        assertEquals(7, tileConfig.getSnakeTail(18));
        assertEquals(16, tileConfig.getSnakeTail(24));
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
} 