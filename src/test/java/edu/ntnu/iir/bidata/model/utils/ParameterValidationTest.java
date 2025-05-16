package edu.ntnu.iir.bidata.model.utils;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.exception.GameException;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;

class ParameterValidationTest {

    @Test
    void validateNonZeroPositiveInteger_WithValidInput_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ParameterValidation.validateNonZeroPositiveInteger(1, "test"));
        assertDoesNotThrow(() -> ParameterValidation.validateNonZeroPositiveInteger(100, "test"));
    }

    @Test
    void validateNonZeroPositiveInteger_WithZero_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validateNonZeroPositiveInteger(0, "test")
        );
        assertEquals("The test needs to be a non-zero positive integer.", exception.getMessage());
    }

    @Test
    void validateNonZeroPositiveInteger_WithNegative_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validateNonZeroPositiveInteger(-1, "test")
        );
        assertEquals("The test needs to be a non-zero positive integer.", exception.getMessage());
    }

    @Test
    void validateGameParameters_WithValidInput_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ParameterValidation.validateGameParameters(1, 2, 10));
    }

    @Test
    void validateGameParameters_WithInvalidDice_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameParameters(0, 2, 10)
        );
        assertEquals("Number of dice must be at least 1", exception.getMessage());
    }

    @Test
    void validateGameParameters_WithInvalidPlayers_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameParameters(1, 1, 10)
        );
        assertEquals("Number of players must be at least 2", exception.getMessage());
    }

    @Test
    void validateGameParameters_WithInvalidBoardSize_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameParameters(1, 2, 9)
        );
        assertEquals("Board size must be at least 10", exception.getMessage());
    }

    @Test
    void validatePlayer_WithValidPlayer_ShouldNotThrowException() {
        Player mockPlayer = Mockito.mock(Player.class);
        assertDoesNotThrow(() -> ParameterValidation.validatePlayer(mockPlayer));
    }

    @Test
    void validatePlayer_WithNull_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validatePlayer(null)
        );
        assertEquals("Player cannot be null", exception.getMessage());
    }

    @Test
    void validateTile_WithValidTile_ShouldNotThrowException() {
        Tile mockTile = Mockito.mock(Tile.class);
        assertDoesNotThrow(() -> ParameterValidation.validateTile(mockTile));
    }

    @Test
    void validateTile_WithNull_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validateTile(null)
        );
        assertEquals("Tile cannot be null", exception.getMessage());
    }

    @Test
    void validateTileId_WithValidId_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ParameterValidation.validateTileId(0));
        assertDoesNotThrow(() -> ParameterValidation.validateTileId(1));
    }

    @Test
    void validateTileId_WithNegativeId_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validateTileId(-1)
        );
        assertEquals("The Tile ID must be a positive integer.", exception.getMessage());
    }

    @Test
    void validateZeroPositiveInteger_WithValidInput_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ParameterValidation.validateZeroPositiveInteger(0, "test"));
        assertDoesNotThrow(() -> ParameterValidation.validateZeroPositiveInteger(1, "test"));
    }

    @Test
    void validateZeroPositiveInteger_WithNegative_ShouldThrowException() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> ParameterValidation.validateZeroPositiveInteger(-1, "test")
        );
        assertEquals("The test must be a positive integer.", exception.getMessage());
    }

    @Test
    void validateGameState_WithValidState_ShouldNotThrowException() {
        Player mockPlayer = Mockito.mock(Player.class);
        assertDoesNotThrow(() -> ParameterValidation.validateGameState(true, mockPlayer));
    }

    @Test
    void validateGameState_WithGameNotPlaying_ShouldThrowException() {
        Player mockPlayer = Mockito.mock(Player.class);
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameState(false, mockPlayer)
        );
        assertEquals("Game is not in progress or no current player", exception.getMessage());
    }

    @Test
    void validateGameState_WithNullPlayer_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameState(true, null)
        );
        assertEquals("Game is not in progress or no current player", exception.getMessage());
    }

    @Test
    void validatePlayersExist_WithValidPlayers_ShouldNotThrowException() {
        Map<String, Player> players = new HashMap<>();
        players.put("Player1", Mockito.mock(Player.class));
        assertDoesNotThrow(() -> ParameterValidation.validatePlayersExist(players));
    }

    @Test
    void validatePlayersExist_WithEmptyMap_ShouldThrowException() {
        Map<String, Player> emptyPlayers = new HashMap<>();
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validatePlayersExist(emptyPlayers)
        );
        assertEquals("No players have been added to the game", exception.getMessage());
    }

    @Test
    void validateGameNotStarted_WithGameNotStarted_ShouldNotThrowException() {
        assertDoesNotThrow(() -> ParameterValidation.validateGameNotStarted(false));
    }

    @Test
    void validateGameNotStarted_WithGameStarted_ShouldThrowException() {
        GameException exception = assertThrows(
            GameException.class,
            () -> ParameterValidation.validateGameNotStarted(true)
        );
        assertEquals("Game is already in progress", exception.getMessage());
    }
} 