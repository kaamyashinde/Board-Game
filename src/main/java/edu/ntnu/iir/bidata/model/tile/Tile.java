package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.utils.ParameterValidation;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Objects;

/**
 * Represents a tile on the game board.
 *
 * @author Durva and Kaamya
 * @version 1.0.1
 */
@Getter
@Setter
public class Tile {
    private final int id;
    private final TileAction action;
    private Tile nextTile;
    private Player currentPlayer;

    /**
     * Constructor that creates an instance of the tile with the desired id and tileAction.
     *
     * @param id     the id of the tile
     * @param action the action associated with the tile
     * @throws IllegalArgumentException if id is negative
     */
    public Tile(int id, TileAction action) {
        ParameterValidation.validateTileId(id);
        this.id = id;
        this.action = action;
        this.currentPlayer = null;
    }

    /**
     * Single-argument constructor (defaults the action to null).
     *
     * @param id The tile's identifier
     * @throws IllegalArgumentException if id is negative
     */
    public Tile(int id) {
        this(id, null);
    }

    /**
     * Sets the next tile in the sequence.
     *
     * @param nextTile The next tile to set
     */
    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
    }

   

    /**
     * Places the player on this tile and performs any associated actions.
     *
     * @param player The player to place on this tile
     * @throws IllegalArgumentException if player is null
     */
    public void landPlayer(Player player) {
        ParameterValidation.validatePlayer(player);
        this.currentPlayer = player;
        performCurrentPlayerAction();
    }

     /**
     * Executes the action associated with this tile.
     *
     * @param player The player who landed on this tile
     * @throws IllegalArgumentException if player is null
     */
    private void performCurrentPlayerAction() {
        if (action != null) {
            action.performAction(currentPlayer);
        }
    }
    /**
     * Removes the player from this tile.
     */
    public void leavePlayer() {
        if (currentPlayer != null) {
            currentPlayer = null;
        }
    }

    /**
     * Checks if this tile is the last tile in the sequence.
     *
     * @return true if this is the last tile, false otherwise
     */
    public boolean isLastTile() {
        return nextTile == null;
    }

    /**
     * Gets the distance to another tile. Uses a HashSet to detect cycles.
     *
     * @param targetTile The tile to calculate distance to
     * @return The number of tiles between this tile and the target tile, or -1 if not reachable
     * @throws IllegalArgumentException if targetTile is null
     */
    public int getDistanceTo(Tile targetTile) {
        ParameterValidation.validateTile(targetTile);
        if (this.equals(targetTile)) {
            return 0;
        }

        int distance = 0;
        Tile current = this;
        HashSet<Tile> visited = new HashSet<>();
        while (current.nextTile != null) {
            distance++;
            current = current.nextTile;
            if (current.equals(targetTile)) {
                return distance;
            }
            if (visited.contains(current)) {
                return -1;
            }
            visited.add(current);
        }
        return -1;
    }

    /**
     * Checks if this tile is equal to another object.
     *
     * @param o The object to compare to
     * @return true if the objects are equal, false otherwise
     */

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

    /**
     * Returns the hash code for this tile.
     *
     * @return the hash code for this tile
     */
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
