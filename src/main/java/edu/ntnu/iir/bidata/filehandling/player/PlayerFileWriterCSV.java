package edu.ntnu.iir.bidata.filehandling.player;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.player.Player;

public class PlayerFileWriterCSV implements PlayerFileWriter {
    @Override
    public void writePlayer(Player player, Path filePath) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            writer.write(player.getName() + "," + (player.getTokenImage() != null ? player.getTokenImage() : "") + "\n");
        } catch (IOException e) {
            throw new RuntimeException("Failed to write player to CSV file", e);
        }
    }

    @Override
    public void writePlayers(java.util.List<Player> players, Path filePath) {
        try (FileWriter writer = new FileWriter(filePath.toFile())) {
            players.forEach(player -> {
                try {
                    writer.write(player.getName() + "," + (player.getTokenImage() != null ? player.getTokenImage() : "") + "\n");
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("Failed to write players to CSV file", e);
        }
    }
}