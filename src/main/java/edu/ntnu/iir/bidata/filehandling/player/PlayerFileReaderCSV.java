package edu.ntnu.iir.bidata.filehandling.player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.player.Player;

public class PlayerFileReaderCSV implements PlayerFileReader {
    @Override
    public Player readPlayer(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                return new Player(line.trim());
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Failed to read player from CSV file", e);
        }
    }

    @Override
    public java.util.List<Player> readPlayers(Path filePath) {
        java.util.List<Player> players = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    players.add(new Player(line.trim()));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read players from CSV file", e);
        }
        return players;
    }
}