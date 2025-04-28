package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * Represents a tile on the game board.
 *
 * @author Durva and Kaamya
 * @version 1.0.0
 */
@Getter
@Setter
public class Tile {
    private final int id;
    private final TileAction action;
    private Tile nextTile;
    private Tile previousTile;

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
     * Sets the next tile in the sequence and updates the previous tile reference.
     *
     * @param nextTile The next tile to set
     */
    public void setNextTile(Tile nextTile) {
        this.nextTile = nextTile;
        if (nextTile != null) {
            nextTile.previousTile = this;
        }
    }

    /**
     * Executes the action associated with this tile.
     *
     * @param player The player who landed on this tile
     * @throws IllegalArgumentException if player is null
     */
    public void performAction(Player player) {
        ParameterValidation.validatePlayer(player);
        if (action != null) {
            action.performAction(player);
        }
    }

    /**
     * Places the player on this tile and performs any associated actions.
     *
     * @param player The player to place on this tile
     * @throws IllegalArgumentException if player is null
     */
    public void landPlayer(Player player) {
        ParameterValidation.validatePlayer(player);
        player.placeOnTile(this);
        performAction(player);
    }

    /**
     * Removes the player from this tile.
     *
     * @param player The player to remove from this tile
     * @throws IllegalArgumentException if player is null
     */
    public void leavePlayer(Player player) {
        ParameterValidation.validatePlayer(player);
        // Additional cleanup logic can be added here if needed
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
     * Checks if this tile is the first tile in the sequence.
     *
     * @return true if this is the first tile, false otherwise
     */
    public boolean isFirstTile() {
        return previousTile == null;
    }

    /**
     * Gets the distance to another tile.
     *
     * @param targetTile The tile to calculate distance to
     * @return The number of tiles between this tile and the target tile, or -1 if not reachable
     * @throws IllegalArgumentException if targetTile is null
     */
    public int getDistanceTo(Tile targetTile) {
        ParameterValidation.validateTile(targetTile);
        if (this == targetTile) {
            return 0;
        }

        int distance = 0;
        Tile current = this;
        while (current.nextTile != null) {
            distance++;
            current = current.nextTile;
            if (current == targetTile) {
                return distance;
            }
        }
        return -1;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tile tile = (Tile) o;
        return id == tile.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
