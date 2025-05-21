package edu.ntnu.iir.bidata.filehandling.player;

import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.player.Player;

public interface PlayerFileReader {
    public Player readPlayer(Path filePath);

    /**
     * Reads all players from a CSV file (one name per line).
     * @param filePath the path to the CSV file
     * @return a list of Player objects
     */
    public java.util.List<Player> readPlayers(java.nio.file.Path filePath);
}
