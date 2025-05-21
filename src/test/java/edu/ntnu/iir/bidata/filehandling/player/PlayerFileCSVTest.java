package edu.ntnu.iir.bidata.filehandling.player;

import org.junit.jupiter.api.Test;

import edu.ntnu.iir.bidata.model.player.Player;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PlayerFileCSVTest {
    @Test
    void testWriteAndReadPlayer() throws Exception {
        Player player = new Player("Arne", "token_red.png");
        Path tempFile = Files.createTempFile("test-player", ".csv");

        // Write player
        new PlayerFileWriterCSV().writePlayer(player, tempFile);

        // Read player
        Player loaded = new PlayerFileReaderCSV().readPlayer(tempFile);

        assertNotNull(loaded);
        assertEquals(player.getName(), loaded.getName());
        assertEquals(player.getTokenImage(), loaded.getTokenImage());

        Files.deleteIfExists(tempFile);
    }
} 