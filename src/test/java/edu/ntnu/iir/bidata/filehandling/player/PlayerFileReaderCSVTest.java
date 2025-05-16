package edu.ntnu.iir.bidata.filehandling.player;

import edu.ntnu.iir.bidata.model.Player;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class PlayerFileReaderCSVTest {
    private PlayerFileReaderCSV reader;
    private Path tempFile;

    @BeforeEach
    void setUp() {
        reader = new PlayerFileReaderCSV();
    }

    @AfterEach
    void tearDown() throws IOException {
        if (tempFile != null && Files.exists(tempFile)) {
            Files.delete(tempFile);
        }
    }

    @Test
    void readPlayer_WithValidFile_ShouldReturnPlayer() throws IOException {
        tempFile = Files.createTempFile("player", ".csv");
        Files.writeString(tempFile, "Alice\n");
        Player player = reader.readPlayer(tempFile);
        assertNotNull(player);
        assertEquals("Alice", player.getName());
    }

    @Test
    void readPlayer_WithEmptyFile_ShouldReturnNull() throws IOException {
        tempFile = Files.createTempFile("player", ".csv");
        Player player = reader.readPlayer(tempFile);
        assertNull(player);
    }

    @Test
    void readPlayers_WithMultiplePlayers_ShouldReturnList() throws IOException {
        tempFile = Files.createTempFile("players", ".csv");
        Files.writeString(tempFile, "Alice\nBob\nCharlie\n");
        List<Player> players = reader.readPlayers(tempFile);
        assertEquals(3, players.size());
        assertEquals("Alice", players.get(0).getName());
        assertEquals("Bob", players.get(1).getName());
        assertEquals("Charlie", players.get(2).getName());
    }

    @Test
    void readPlayers_WithBlankLines_ShouldIgnoreBlanks() throws IOException {
        tempFile = Files.createTempFile("players", ".csv");
        Files.writeString(tempFile, "Alice\n\nBob\n  \nCharlie\n");
        List<Player> players = reader.readPlayers(tempFile);
        assertEquals(3, players.size());
        assertEquals("Alice", players.get(0).getName());
        assertEquals("Bob", players.get(1).getName());
        assertEquals("Charlie", players.get(2).getName());
    }

    @Test
    void readPlayer_FileNotFound_ShouldThrowRuntimeException() {
        Path nonExistent = Path.of("/tmp/nonexistent_file.csv");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reader.readPlayer(nonExistent));
        assertTrue(ex.getMessage().contains("Failed to read player from CSV file"));
    }

    @Test
    void readPlayers_FileNotFound_ShouldThrowRuntimeException() {
        Path nonExistent = Path.of("/tmp/nonexistent_file.csv");
        RuntimeException ex = assertThrows(RuntimeException.class, () -> reader.readPlayers(nonExistent));
        assertTrue(ex.getMessage().contains("Failed to read players from CSV file"));
    }
} 