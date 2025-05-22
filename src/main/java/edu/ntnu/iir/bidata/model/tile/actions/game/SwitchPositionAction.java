package edu.ntnu.iir.bidata.model.tile.actions.game;

import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.TileAction;
import java.util.List;

/**
 * A tile action that allows a player to switch positions with the player in front of them. This
 * action adds a competitive element to the game by allowing players to catch up to others who are
 * ahead. The action will only take effect if there is at least one player ahead of the current
 * player.
 *
 * @author kaamyashinde
 * @version 1.0
 */
public class SwitchPositionAction implements TileAction {

  private final List<Player> allPlayers;

  /**
   * Constructs a new SwitchPositionAction with the list of all players in the game. This list is
   * needed to determine which player is in front of the current player.
   *
   * @param allPlayers The list of all players participating in the game
   */
  public SwitchPositionAction(List<Player> allPlayers) {
    this.allPlayers = allPlayers;
  }

  /**
   * Gets the list of all players in the game.
   *
   * @return The list of all players
   */
  public List<Player> getAllPlayers() {
    return allPlayers;
  }

  /**
   * Executes the position switch action. If there is a player ahead of the current player, their
   * positions will be swapped. If there is no player ahead, no action is taken.
   *
   * @param currentPlayer The player who landed on the tile
   * @param currentTile The tile the player landed on
   */
  @Override
  public void executeAction(Player currentPlayer, Tile currentTile) {
    Player playerInFront = findPlayerInFront(currentPlayer);
    if (playerInFront != null) {
      Tile tempTile = currentPlayer.getCurrentTile();
      currentPlayer.setCurrentTile(playerInFront.getCurrentTile());
      playerInFront.setCurrentTile(tempTile);
    }
  }

  /**
   * Returns a description of the action that will be performed. This description is used to inform
   * players about what will happen when they land on a tile with this action.
   *
   * @return A string describing the action
   */
  @Override
  public String getDescription() {
    return "Switch position with the player in front of you";
  }

  /**
   * Helper method to find the player who is closest in front of the current player. This method
   * searches through all players and finds the one who is ahead of the current player by the
   * smallest number of positions.
   *
   * @param currentPlayer The player whose position we're checking from
   * @return The player who is closest in front of the current player, or null if no player is ahead
   */
  private Player findPlayerInFront(Player currentPlayer) {
    int currentPosition = currentPlayer.getCurrentPosition();
    return allPlayers.stream()
        .filter(otherPlayer -> otherPlayer != currentPlayer)
        .filter(otherPlayer -> otherPlayer.getCurrentPosition() > currentPosition)
        .min(
            java.util.Comparator.comparingInt(
                otherPlayer -> otherPlayer.getCurrentPosition() - currentPosition))
        .orElse(null);
  }
}
