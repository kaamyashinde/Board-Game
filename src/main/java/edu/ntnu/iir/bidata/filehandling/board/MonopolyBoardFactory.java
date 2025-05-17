package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;

public class MonopolyBoardFactory {
    private static final int BOARD_SIZE = 20;

    public static Board createBoard() {
        Board board = new Board(BOARD_SIZE);

        // Create and add all tiles
        createAndAddTiles(board);

        // Fill empty positions with BlankTile
        for (int i = 0; i < BOARD_SIZE; i++) {
            if (board.getTile(i) == null) {
                board.addTile(new BlankTile(i));
            }
        }

        // Connect tiles in a circle
        connectTilesCircular(board);

        return board;
    }

    private static void createAndAddTiles(Board board) {
        // GO (top-left corner)
        board.addTile(new GoTile(0));
        // Top row (positions 1-4)
        addPropertyGroup(board, 1, 4, 0, 100, 20);
        // GO TO JAIL (top-right corner)
        board.addTile(new Tile(5, new GoToJailAction(15)));
        // Right column (positions 6-9)
        addPropertyGroup(board, 6, 9, 1, 150, 30);
        // FREE PARKING (bottom-right corner)
        board.addTile(new FreeParkingTile(10));
        // Bottom row (positions 11-14)
        addPropertyGroup(board, 11, 14, 2, 200, 40);
        // JAIL (bottom-left corner)
        board.addTile(new JailTile(15));
        // Left column (positions 16-19)
        addPropertyGroup(board, 16, 19, 3, 250, 50);
    }

    private static void addPropertyGroup(Board board, int startPos, int endPos,
                                       int group, int price, int rent) {
        for (int pos = startPos; pos <= endPos; pos++) {
            board.addTile(new PropertyTile(pos, price, rent, group));
        }
    }

    private static void connectTilesCircular(Board board) {
        // Connect all tiles in a circle
        for (int i = 0; i < BOARD_SIZE - 1; i++) {
            board.connectTiles(i, board.getTile(i + 1));
        }
        // Connect last tile back to first tile
        board.connectTiles(BOARD_SIZE - 1, board.getTile(0));
    }
}

class BlankTile extends Tile {
    public BlankTile(int id) {
        super(id);
    }
    @Override
    public String toString() {
        return "BlankTile(" + getId() + ")";
    }
}
