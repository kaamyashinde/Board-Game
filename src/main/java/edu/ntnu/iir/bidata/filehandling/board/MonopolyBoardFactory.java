package edu.ntnu.iir.bidata.filehandling.board;

import edu.ntnu.iir.bidata.model.board.Board;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;

public class MonopolyBoardFactory {
    private static final int BOARD_SIZE = 20;

    public static Board createBoard() {
        Board board = new Board(BOARD_SIZE);

        // Create and add all tiles
        createAndAddTiles(board);

        // Connect tiles in a circle
        connectTilesCircular(board);

        return board;
    }

    private static void createAndAddTiles(Board board) {
        // Add Go tile (position 0)
        board.addTile(new GoTile(0));

        // Add properties
        addPropertyGroup(board, 1, 2, 0, 100, 20);    // Positions 1-2
        addPropertyGroup(board, 3, 4, 1, 150, 30);   // Positions 3-4
        addPropertyGroup(board, 5, 6, 2, 200, 40);  // Positions 5-6
        addPropertyGroup(board, 7, 8, 3, 250, 50); // Positions 7-8

        // Add Jail (position 10)
        board.addTile(new JailTile(10));

        // Add Free Parking (position 15)
        board.addTile(new FreeParkingTile(15));

        // Add remaining properties
        addPropertyGroup(board, 11, 12, 0, 100, 20);    // Positions 11-12
        addPropertyGroup(board, 13, 14, 1, 150, 30);   // Positions 13-14
        addPropertyGroup(board, 16, 17, 2, 200, 40);  // Positions 16-17
        addPropertyGroup(board, 18, 19, 3, 250, 50); // Positions 18-19
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
