package edu.ntnu.iir.bidata.filehandling.player;

import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.Player;

public interface PlayerFileWriter {
    public void writePlayer(Player player, Path filePath);
}
