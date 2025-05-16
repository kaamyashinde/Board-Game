package edu.ntnu.iir.bidata.model.tile.core;

import edu.ntnu.iir.bidata.model.Player;

public interface TileAction {

  void executeAction(Player player, Tile currentTile);

  String getDescription();
}
