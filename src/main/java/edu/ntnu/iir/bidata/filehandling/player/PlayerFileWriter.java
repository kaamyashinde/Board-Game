package edu.ntnu.iir.bidata.filehandling.player;

import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.player.Player;

public interface PlayerFileWriter {
    public void writePlayer(Player player, Path filePath);

    /**
     * Writes all players to a CSV file (one name per line).
     * @param players the list of players
     * @param filePath the path to the CSV file
     */
    public void writePlayers(java.util.List<Player> players, java.nio.file.Path filePath);
}
