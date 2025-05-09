package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.model.Board;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.Tile;
import edu.ntnu.iir.bidata.model.tile.TileAction;

import java.util.List;
import java.util.Map;

/**
 * Handles console output for the game.
 */
public class ConsoleOutputHandler {
    private Board board;
    private Map<Player, Integer> playerPositions;

    public void setBoard(Board board) {
        this.board = board;
    }

    public void setPlayerPositions(Map<Player, Integer> playerPositions) {
        this.playerPositions = playerPositions;
    }

    public void displayWelcomeMessage() {
        System.out.println("=== Welcome to the Board Game! ===");
        System.out.println("Get ready to roll the dice and race to the finish!");
        System.out.println("---------------------------------");
    }

    public void displayPlayerTurn(Player player) {
        System.out.println("\n-----------");
        System.out.println("Current player: " + player.getName());
        System.out.println("Current position: " + player.getCurrentTile().getId());
    }

    public void displayDiceRoll(Player player, int rollResult) {
        System.out.println(player.getName() + " rolled: " + rollResult);
    }

    public void displayBoard() {
        if (board == null || playerPositions == null) {
            System.out.println("Board not initialized yet!");
            return;
        }

        System.out.println("\nCurrent Board State:");
        System.out.println("-----------");
        
        // Display board in a grid format
        int size = board.getSize();
        int tilesPerRow = 5;
        
        for (int i = 0; i < size; i += tilesPerRow) {
            // Print tile numbers
            for (int j = 0; j < tilesPerRow && (i + j) < size; j++) {
                System.out.printf("%3d ", i + j);
            }
            System.out.println();
            
            // Print players on tiles
            for (int j = 0; j < tilesPerRow && (i + j) < size; j++) {
                int tileId = i + j;
                StringBuilder tileContent = new StringBuilder("   ");
                
                // Add players on this tile
                for (Map.Entry<Player, Integer> entry : playerPositions.entrySet()) {
                    if (entry.getValue() == tileId) {
                        tileContent.append(entry.getKey().getName().charAt(0));
                    }
                }
                
                // Add special tile indicators
                Tile tile = board.getPositionOnBoard(tileId);
                if (tile.getAction() != null) {
                    tileContent.append("*");
                }
                
                System.out.printf("%3s ", tileContent.toString());
            }
            System.out.println();
            System.out.println();
        }
    }

    public void displayWinner(Player winner) {
        System.out.println("\n🎉 Player " + winner.getName() + " has reached the final tile and wins the game! 🎉");
    }

    public void displaySeparator() {
        System.out.println("-----------");
    }

    public void displayTileAction(Player player, TileAction action) {
        System.out.println("\n✨ Special Tile Action! ✨");
        System.out.println("Player: " + player.getName());
        System.out.println("Action: " + action.getDescription());
        System.out.println("-----------");
    }
} 