package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import lombok.Getter;
import lombok.Setter;

/**
 * Class that handles the movements of a specific player.
 * @author kaamyashinde
 * @version 0.0.2
 */
@Getter
@Setter
public class Player {
    private String name;
    private Tile currentTile;
    private boolean skipNextTurn;

    /**
     * The constructor that initialises the players with their name and the board game they are connected to.
     * @param inputName the name of the player
     * @param game the instance of Board Game they are connected to todo
     */
    public Player(String inputName) {
        setName(inputName);
    }

    /**
     * The method that allows the player to move on the board.
     * @param steps the number of steps the player will move
     */
    public void move(int steps) {
        currentTile = currentTile.getNextTile(steps);
    }

    /**
     * The method that checks if the player is on the first tile.
     * @return true if the player is on the first tile, false otherwise
     */
    public boolean isOnFirstTile() {
      return currentTile.isFirstTile();
    }

    /**
     * The method that checks if the player is on the last tile.
     * @return true if the player is on the last tile, false otherwise
     */
    public boolean isOnLastTile() {
        return currentTile.isLastTile();
    }

    /**
     * Checks the current position of the player.
     * @return the current position of the player
     */
    public int getCurrentPosition() {
        return currentTile != null ? currentTile.getId() : -1;
    }
    
}