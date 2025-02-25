package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;

public interface TileAction {
  void performAction (Player player);
  String getDescription();
}
