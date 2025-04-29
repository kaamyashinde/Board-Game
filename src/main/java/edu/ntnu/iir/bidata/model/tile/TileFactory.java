package edu.ntnu.iir.bidata.model.tile;

import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.LadderAction;
import edu.ntnu.iir.bidata.model.tile.snakeandladder.SnakeAction;
import edu.ntnu.iir.bidata.model.utils.ParameterValidation;

import java.util.List;

/**
 * Factory class for creating tiles with special actions.
 */
public class TileFactory {
    private final List<Player> players;
    private final TileConfiguration tileConfig;

    public TileFactory(List<Player> players, TileConfiguration tileConfig) {
        this.players = players;
        this.tileConfig = tileConfig;
    }

    public Tile createTile(int position) {
        TileAction action = createSpecialAction(position);
        return new Tile(position, action);
    }

    private TileAction createSpecialAction(int position) {
        if (tileConfig.isLadderStart(position)) {
            return new LadderAction(tileConfig.getLadderEnd(position));
        } else if (tileConfig.isSnakeHead(position)) {
            return new SnakeAction(tileConfig.getSnakeTail(position));
        } else if (tileConfig.isSkipTurn(position)) {
            return new SkipTurnAction();
        } else if (tileConfig.isMoveBack(position)) {
            return new MoveBackAction(tileConfig.getMoveBackSteps(position));
        } else if (tileConfig.isSwitchPlaces(position) && !players.isEmpty()) {
            return new SwitchPlacesAction(players.get(0));
        }
        return null;
    }
} 