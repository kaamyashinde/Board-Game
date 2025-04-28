package edu.ntnu.iir.bidata.view;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import java.util.Random;

/**
 * JavaFX application that displays a board game simulation.
 */
public class JavaFXBoardGameLauncher extends Application {
    private static final int BOARD_SIZE = 100;
    private static final int GRID_SIZE = 10; // 10x10 grid for visualization
    private static final int SQUARE_SIZE = 50;
    private final Random random = new Random();
    
    private Shape player1;
    private Shape player2;
    private int player1Position = 0;
    private int player2Position = 0;

    @Override
    public void start(Stage primaryStage) {
        // Create the main layout
        BorderPane root = new BorderPane();
        
        // Create the board
        GridPane board = createBoard();
        root.setCenter(board);
        
        // Create the control panel
        HBox controls = new HBox(10);
        controls.setPadding(new Insets(10));
        Button simulateButton = new Button("Simulate Game");
        simulateButton.setOnAction(e -> simulateMove(board));
        controls.getChildren().add(simulateButton);
        root.setBottom(controls);
        
        // Create the scene
        Scene scene = new Scene(root, GRID_SIZE * SQUARE_SIZE + 20, GRID_SIZE * SQUARE_SIZE + 100);
        
        // Set up the stage
        primaryStage.setTitle("Board Game Simulation");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private GridPane createBoard() {
        GridPane board = new GridPane();
        
        // Create the board squares
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                int position = row * GRID_SIZE + col;
                
                // Create a stack pane to hold both the square and the number
                StackPane tile = new StackPane();
                
                // Create the square with appropriate color
                Rectangle square = new Rectangle(SQUARE_SIZE, SQUARE_SIZE);
                square.setStroke(Color.BLACK);
                
                // Set color based on position
                if (position % 10 == 0) {
                    square.setFill(Color.LIGHTGREEN); // Start and every 10th tile
                } else if (position % 5 == 0) {
                    square.setFill(Color.LIGHTBLUE); // Every 5th tile
                } else {
                    square.setFill(Color.WHITE); // Regular tiles
                }
                
                // Create and style the number text
                Text number = new Text(String.valueOf(position));
                number.setStyle("-fx-font-weight: bold;");
                
                // Add both to the stack pane
                tile.getChildren().addAll(square, number);
                
                // Add the tile to the board
                board.add(tile, col, row);
            }
        }
        
        // Create and add players
        player1 = new Circle(SQUARE_SIZE/2, SQUARE_SIZE/2, SQUARE_SIZE/3, Color.RED);
        player2 = new Circle(SQUARE_SIZE/2, SQUARE_SIZE/2, SQUARE_SIZE/3, Color.BLUE);
        
        // Add players to starting position
        board.add(player1, 0, 0);
        board.add(player2, 0, 0);
        
        return board;
    }

    private void simulateMove(GridPane board) {
        // Simulate dice roll (1-6)
        int player1Move = random.nextInt(6) + 1;
        int player2Move = random.nextInt(6) + 1;
        
        // Update positions
        player1Position = Math.min(player1Position + player1Move, BOARD_SIZE - 1);
        player2Position = Math.min(player2Position + player2Move, BOARD_SIZE - 1);
        
        // Calculate grid positions
        int player1Row = player1Position / GRID_SIZE;
        int player1Col = player1Position % GRID_SIZE;
        int player2Row = player2Position / GRID_SIZE;
        int player2Col = player2Position % GRID_SIZE;
        
        // Update player positions on the board
        board.getChildren().remove(player1);
        board.getChildren().remove(player2);
        board.add(player1, player1Col, player1Row);
        board.add(player2, player2Col, player2Row);
        
        // Check for winner
        if (player1Position >= BOARD_SIZE - 1 || player2Position >= BOARD_SIZE - 1) {
            String winner = player1Position >= BOARD_SIZE - 1 ? "Player 1" : "Player 2";
            System.out.println(winner + " wins!");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
} 