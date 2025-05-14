package edu.ntnu.iir.bidata.view;

import edu.ntnu.iir.bidata.controller.GameController;
import edu.ntnu.iir.bidata.model.Player;
import edu.ntnu.iir.bidata.model.tile.TileAction;
import javafx.animation.PauseTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.logging.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class SnakesAndLaddersGameUI {
    private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersGameUI.class.getName());
    private final Stage primaryStage;
    private DiceView diceView;
    private final Map<String, Circle> playerTokenMap = new HashMap<>();
    private final Map<String, Label> playerPositionLabels = new HashMap<>();
    private Pane playerLayer;
    private VBox playerPanel;
    private Button rollDiceBtn;
    private Label statusLabel;
    private List<Player> playerNames;
    private GameController controller;

    private final int TILE_SIZE = 50;
    private final int BOARD_SIZE = 10; // 10x10 board

    private final Color[] PLAYER_COLORS = {
        Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE
    };

    /**
     * Constructor that receives the selected players from the menu
     *
     * @param primaryStage The primary stage
     * @param playerNames List of player names selected in the menu
     */
    public SnakesAndLaddersGameUI(Stage primaryStage, List<Player> playerNames) {
        LOGGER.info("Initializing Snakes and Ladders Game UI with players: " + playerNames);
        this.primaryStage = primaryStage;
        this.playerNames = playerNames;

        // If no players were passed, add a default player
        if (this.playerNames == null || this.playerNames.isEmpty()) {
            LOGGER.warning("No players provided, adding default player");
            this.playerNames = new ArrayList<>();
            this.playerNames.add(new Player("Player 1"));
        }

        setupGamePage();
        initializePlayerPositions();
    }

    /**
     * Sets the game controller
     * @param controller The game controller
     */
    public void setController(GameController controller) {
        LOGGER.info("Setting game controller");
        this.controller = controller;
        controller.setPlayerNames(playerNames.stream().map(Player::getName).collect(Collectors.toList()));
        updateCurrentPlayerIndicator(controller.getCurrentSnakesAndLaddersPlayerName());
    }

    private void setupGamePage() {
        LOGGER.info("Setting up game page");
        primaryStage.setTitle("Snakes & Ladders - Game");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f5fff5;");

        // --- Board (center) ---
        StackPane boardPane = new StackPane();
        boardPane.setAlignment(Pos.CENTER_LEFT);

        // Load custom board image
        Image boardImage = new Image(
            Objects.requireNonNull(getClass().getResourceAsStream("/snakes_and_ladders_board.jpeg")));
        ImageView boardView = new ImageView(boardImage);
        boardView.setFitWidth(TILE_SIZE * BOARD_SIZE);
        boardView.setFitHeight(TILE_SIZE * BOARD_SIZE);
        boardView.setPreserveRatio(false);

        // Add the board image to the pane
        boardPane.getChildren().add(boardView);

        // Create a pane for player tokens that will be positioned over the board
        playerLayer = new Pane();
        playerLayer.setPrefSize(boardView.getFitWidth(), boardView.getFitHeight());
        boardPane.getChildren().add(playerLayer);

        root.setCenter(boardPane);

        // --- Right: Player info panel ---
        playerPanel = new VBox(15);
        playerPanel.setPadding(new Insets(20));
        playerPanel.setStyle("-fx-background-color: #e6fff2; -fx-background-radius: 20;");
        playerPanel.setPrefWidth(250);
        playerPanel.setAlignment(Pos.TOP_LEFT);

        // Add status label at the top of the player panel
        statusLabel = new Label("Game Started!");
        statusLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");
        statusLabel.setWrapText(true);

        playerPanel.getChildren().add(statusLabel);
        playerPanel.getChildren().add(new Label("--------------------"));

        // Create player tokens and labels based on selected players
        for (int i = 0; i < playerNames.size(); i++) {
            String playerName = playerNames.get(i).getName();

            // Create player info section
            VBox playerBox = new VBox(5);
            playerBox.setStyle("-fx-padding: 5px;");

            Label playerLabel = new Label(playerName.toUpperCase() + ":");
            playerLabel.setFont(Font.font("System", FontWeight.BOLD, 14));

            // Position label
            Label posLabel = new Label("at position: 0");
            playerPositionLabels.put(playerName, posLabel);

            Circle token = createPlayerToken(i + 1, playerName);
            playerTokenMap.put(playerName, token);

            HBox nameBox = new HBox(10);
            nameBox.setAlignment(Pos.CENTER_LEFT);
            Circle colorIndicator = new Circle(8);
            colorIndicator.setFill(PLAYER_COLORS[i % PLAYER_COLORS.length]);
            colorIndicator.setStroke(Color.BLACK);
            colorIndicator.setStrokeWidth(1);
            nameBox.getChildren().addAll(colorIndicator, playerLabel);

            playerBox.getChildren().addAll(nameBox, posLabel);
            playerPanel.getChildren().add(playerBox);
        }

        root.setRight(playerPanel);

        // --- Top: Roll dice and status ---
        VBox topBox = new VBox(10);
        topBox.setAlignment(Pos.CENTER_LEFT);
        topBox.setPadding(new Insets(10, 0, 10, 20));

        HBox diceBox = new HBox(20);
        diceBox.setAlignment(Pos.CENTER_LEFT);

        rollDiceBtn = new Button("ROLL DICE");
        rollDiceBtn.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 18px; -fx-background-radius: 15;");

        diceView = new DiceView();
        diceBox.getChildren().addAll(rollDiceBtn, diceView);
        rollDiceBtn.setOnAction(e -> rollDiceAndMove());

        topBox.getChildren().add(diceBox);
        root.setTop(topBox);

        Scene scene = new Scene(root, 1100, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Initialize all player positions to the starting position
     */
    private void initializePlayerPositions() {
        LOGGER.info("Initializing player positions");
        for (Player player : playerNames) {
            // Move token to starting position
            movePlayerToken(player.getName(), 0);
        }
    }

    /**
     * Roll the dice and move the current player
     */
    private void rollDiceAndMove() {
        if (controller == null) return;

        rollDiceBtn.setDisable(true);

        String currentPlayer = controller.getCurrentSnakesAndLaddersPlayerName();

        int roll = controller.rollDiceForSnakesAndLadders();
        diceView.setValue(roll);

        statusLabel.setText(currentPlayer + " rolled a " + roll + "!");

        PauseTransition pause = new PauseTransition(Duration.millis(800));
        pause.setOnFinished(event -> {
            boolean hasWon = controller.updateSnakesAndLaddersPosition(currentPlayer, roll);

            // Update the UI with new position
            updatePlayerPosition(currentPlayer);

            // Check for win condition
            if (hasWon) {
                showWinner(new Player(currentPlayer));
                return;
            }

            // Move to next player
            controller.nextSnakesAndLaddersPlayer();
            updateCurrentPlayerIndicator(controller.getCurrentSnakesAndLaddersPlayerName());

            // Re-enable roll button
            rollDiceBtn.setDisable(false);
        });
        pause.play();
    }

    /**
     * Updates the current player indicator in the UI
     */
    public void updateCurrentPlayerIndicator(String currentPlayer) {
        LOGGER.info("Updating current player indicator: " + currentPlayer);
        statusLabel.setText(currentPlayer + "'s Turn");
    }

    /**
     * Creates a player token with the specified color
     * @param playerNumber the player number (1-based)
     * @param playerName the player's name
     * @return the Circle representing the player token
     */
    private Circle createPlayerToken(int playerNumber, String playerName) {
        Circle token = new Circle(10);
        token.setFill(PLAYER_COLORS[(playerNumber - 1) % PLAYER_COLORS.length]);
        token.setStroke(Color.BLACK);
        token.setStrokeWidth(2);

        // Add token to the player layer
        playerLayer.getChildren().add(token);

        // Position token off-board initially
        token.setTranslateX(20); // off the board
        token.setTranslateY(20); // off the board

        return token;
    }

    /**
     * Updates the position of a player in the UI
     * @param playerName the player's name
     */
    private void updatePlayerPosition(String playerName) {
        if (controller == null) return;

        // Get current position from controller
        int position = controller.getPlayerPosition(playerName);

        // Update position label
        Label positionLabel = playerPositionLabels.get(playerName);
        positionLabel.setText("at position: " + position);

        // Move the token on the board
        movePlayerToken(playerName, position);
    }

    /**
     * Moves a player token to a specific position on the board
     * @param playerName the player's name
     * @param position the board position (1-100)
     */
    private void movePlayerToken(String playerName, int position) {
        Circle token = playerTokenMap.get(playerName);
        if (token == null) {
            return;
        }

        if (position == 0) {
            // Starting position (off board)
            token.setTranslateX(20);
            token.setTranslateY(TILE_SIZE * BOARD_SIZE + 20);
            return;
        }

        if (position < 1 || position > 100) {
            return;
        }

        // Calculate coordinates for the position
        int[] coordinates = getCoordinatesForPosition(position);

        // Add a small offset based on player index to prevent complete overlap
        int playerIndex = playerNames.indexOf(playerName);
        int offsetX = playerIndex * 5 - 5;
        int offsetY = playerIndex * 5 - 5;

        // Move the token
        token.setTranslateX(coordinates[0] + offsetX);
        token.setTranslateY(coordinates[1] + offsetY);
    }

    /**
     * Display a message about a snake or ladder
     */
    public void displaySnakeOrLadderMessage(String playerName, int fromPosition, int toPosition, String type) {
        statusLabel.setText(playerName + " hit a " + type + "! Moving from " + fromPosition + " to " + toPosition);

        // Add a short delay before actually moving the token
        PauseTransition pause = new PauseTransition(Duration.millis(1000));
        pause.setOnFinished(e -> {
            // Move the token to the new position
            movePlayerToken(playerName, toPosition);
        });
        pause.play();
    }

    /**
     * Maps a board position (1-100) to pixel coordinates on the board image
     * @param position the board position (1-100)
     * @return x,y coordinates for the position on the board
     */
    public int[] getCoordinatesForPosition(int position) {
        if (position < 1 || position > 100) {
            throw new IllegalArgumentException("Position must be between 1 and 100");
        }

        int row = (position - 1) / BOARD_SIZE;
        int col;

        // Handle snake and ladder board row alternating direction
        if (row % 2 == 0) { // Even rows (0, 2, 4, 6, 8) go left to right
            col = (position - 1) % BOARD_SIZE;
        } else { // Odd rows (1, 3, 5, 7, 9) go right to left
            col = BOARD_SIZE - 1 - ((position - 1) % BOARD_SIZE);
        }

        // Flip row because the board starts from the bottom
        row = BOARD_SIZE - row;

        // Calculate pixel coordinates (adding offset to center token in tile)
        int x = col * TILE_SIZE + TILE_SIZE / 2;
        int y = row * TILE_SIZE + TILE_SIZE / 2;

        return new int[] {x, y};
    }

    // Remove all @Override methods and replace with regular methods
    public void showWelcomeMessage() {
        statusLabel.setText("Welcome to Snakes & Ladders!");
    }

    public void showPlayerTurn(Player player) {
        statusLabel.setText(player.getName() + "'s Turn");
    }

    public void showDiceRoll(Player player, int rollResult) {
        statusLabel.setText(player.getName() + " rolled a " + rollResult);
        diceView.setValue(rollResult);
    }

    public void updateBoard() {
        // The board is already displayed in the UI
        // This would update all player positions based on the model
        if (controller != null) {
            for (Player player : playerNames) {
                updatePlayerPosition(player.getName());
            }
        }
    }

    public void showWinner(Player winner) {
        statusLabel.setText("🏆 " + winner.getName() + " WINS! 🏆");
        rollDiceBtn.setDisable(true);

        // Create a winner announcement
        Stage winnerStage = new Stage();
        winnerStage.initOwner(primaryStage);
        winnerStage.setTitle("Game Over");

        VBox winBox = new VBox(20);
        winBox.setAlignment(Pos.CENTER);
        winBox.setPadding(new Insets(30));
        winBox.setStyle("-fx-background-color: #e6fff2;");

        Label winLabel = new Label(winner.getName() + " WINS!");
        winLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");

        Button okButton = new Button("OK");
        okButton.setStyle("-fx-background-color: #bdebc8; -fx-font-size: 16px;");
        okButton.setOnAction(e -> winnerStage.close());

        winBox.getChildren().addAll(winLabel, okButton);

        Scene winScene = new Scene(winBox, 300, 200);
        winnerStage.setScene(winScene);
        winnerStage.show();
    }

    public void showTileAction(Player player, TileAction action) {
        statusLabel.setText(player.getName() + " " + action.getDescription());
    }
}