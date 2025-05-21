package edu.ntnu.iir.bidata.filehandling.player;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Path;

import edu.ntnu.iir.bidata.model.player.Player;

public class PlayerFileReaderCSV implements PlayerFileReader {
    @Override
    public Player readPlayer(Path filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String line = reader.readLine();
            if (line != null && !line.isEmpty()) {
                String[] parts = line.split(",", 2);
                String name = parts[0].trim();
                String tokenImage = parts.length > 1 ? parts[1].trim() : null;
                return new Player(name, tokenImage);
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
                    String[] parts = line.split(",", 2);
                    String name = parts[0].trim();
                    String tokenImage = parts.length > 1 ? parts[1].trim() : null;
                    players.add(new Player(name, tokenImage));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read players from CSV file", e);
        }
        return players;
    }

    /**
     * Reads all players from a CSV input stream (one name per line).
     * This method is useful for reading from resources or other input sources.
     *
     * @param inputStream the input stream containing CSV data
     * @return a list of Player objects
     */
    public java.util.List<Player> readPlayersFromInputStream(InputStream inputStream) {
        java.util.List<Player> players = new java.util.ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] parts = line.split(",", 2);
                    String name = parts[0].trim();
                    String tokenImage = parts.length > 1 ? parts[1].trim() : null;
                    players.add(new Player(name, tokenImage));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to read players from input stream", e);
        }
        return players;
    }
}