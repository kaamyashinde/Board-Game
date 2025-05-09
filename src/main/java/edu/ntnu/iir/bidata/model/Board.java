package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileFactory;
import edu.ntnu.iir.bidata.model.tile.TileConfiguration;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import lombok.Getter;

import java.util.HashMap;
import java.util.List;

/**
 * This class contains the actual board where a game can be played.
 *
 * @author kaamyashinde
 * @version 0.0.1
 */
@Getter
public class Board {
    private final HashMap<Integer, Tile> tiles;
    private final int size;

    /**
     * The constructor where the board is initialised with special tiles.
     *
     * @param size The number of tiles the board would contain.
     * @param players The list of players in the game (needed for switch places action)
     */
    public Board(int size, List<Player> players) {
        ParameterValidation.validateNonZeroPositiveInteger(size, "number of tiles to create");
        this.size = size;
        this.tiles = new HashMap<>();
        
        // Create tiles using factory
        TileConfiguration tileConfig = new TileConfiguration();
        TileFactory tileFactory = new TileFactory(players, tileConfig);
        
        // Create all tiles
        for (int i = 0; i < size; i++) {
            tiles.put(i, tileFactory.createTile(i));
        }
        
        // Link tiles using board linker
        BoardLinker boardLinker = new BoardLinker(tiles, size);
        boardLinker.linkTiles();
    }

    /**
     * Access a specific position on the board.
     *
     * @param id the id to be used to access a specific spot on the board.
     * @return the tile at the specified position
     */
    public Tile getPositionOnBoard(int id) {
        return tiles.get(id);
    }
}