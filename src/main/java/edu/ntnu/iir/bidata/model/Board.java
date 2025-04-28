package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import edu.ntnu.iir.bidata.model.tile.SkipTurnAction;
import edu.ntnu.iir.bidata.model.tile.MoveBackAction;
import edu.ntnu.iir.bidata.model.tile.MoveForwardAction;
import edu.ntnu.iir.bidata.model.tile.TeleportAction;
import edu.ntnu.iir.bidata.model.tile.SwitchPlacesAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;
import lombok.Getter;

import java.util.HashMap;

/**
 * This class contains the actual board where a game can be played.
 *
 * @author kaamyashinde
 * @version 0.0.1
 */
@Getter
public class Board {
  private final HashMap<Integer, Tile> tiles;

  /**
   * The constructor where the board is initialised with special tiles.
   *
   * @param numberOfTiles The number of tiles the board would contain.
   * @param players The list of players in the game (needed for switch places action)
   */
  public Board(int numberOfTiles, java.util.List<Player> players) {
    ParameterValidation.validateNonZeroPositiveInteger(numberOfTiles, "number of tiles to create");
    tiles = new HashMap<>();
    
    // Create all tiles with special actions at specific positions
    for (int i = 0; i < numberOfTiles; i++) {
      TileAction action = null;
      
      // Add special tiles at specific positions
      if (i == 3) {
        action = new LadderAction(12); // Ladder from 3 to 12
      } else if (i == 5) {
        action = new SkipTurnAction(); // Skip turn
      } else if (i == 8) {
        action = new SnakeAction(4); // Snake from 8 to 4
      } else if (i == 10) {
        action = new MoveBackAction(3); // Move back 3 spaces
      } else if (i == 12) {
        // Switch places with another player (using the first other player found)
        if (players != null && !players.isEmpty()) {
          action = new SwitchPlacesAction(players.get(0));
        }
      } else if (i == 15) {
        action = new LadderAction(22); // Ladder from 15 to 22
      } else if (i == 18) {
        action = new SnakeAction(7); // Snake from 18 to 7
      } else if (i == 20) {
        action = new MoveBackAction(2); // Move back 2 spaces
      } else if (i == 22) {
        action = new LadderAction(25); // Ladder from 22 to 25
      } else if (i == 24) {
        action = new SnakeAction(16); // Snake from 24 to 16
      }
      
      tiles.put(i, new Tile(i, action));
    }

    // Link the tiles: set each tile's nextTile except for the final tile.
    for (int i = 0; i < numberOfTiles - 1; i++) {
      tiles.get(i).setNextTile(tiles.get(i + 1));
    }
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