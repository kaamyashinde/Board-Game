package edu.ntnu.iir.bidata.view.monopoly;

import edu.ntnu.iir.bidata.controller.MonopolyController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.GoTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.JailTile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.FreeParkingTile;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.util.*;
import java.util.logging.Logger;

/**
 * JavaFX UI implementation for the Monopoly game.
 */
public class MonopolyGameUI extends JavaFXGameUI {
    private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
    private static final int GRID_DIM = 6; // 6x6 grid for 20-tile Monopoly
    private static final int TILE_SIZE = 60;
    private final Map<Integer, StackPane> tilePanes = new HashMap<>();
    private final Map<Player, Circle> playerTokens = new HashMap<>();
    private final Button rollDiceButton = new Button("Roll Dice");
    private final MonopolyController controller;
    private final Color BLANK_COLOR = Color.LIGHTGRAY;
    private final Color[] GROUP_COLORS = { Color.SADDLEBROWN, Color.LIGHTBLUE, Color.HOTPINK, Color.ORANGE };
    private final Color GO_COLOR = Color.LIMEGREEN;
    private final Color JAIL_COLOR = Color.DARKGRAY;
    private final Color FREE_PARKING_COLOR = Color.GOLD;
    private final BorderPane mainLayout;
    private final GridPane boardPane;
    private final VBox playerInfoPanel;
    private final HBox gameControls;
    private final Label diceLabel = new Label("Dice: -");

    public MonopolyGameUI(BoardGame boardGame) {
        super(boardGame);
        this.controller = new MonopolyController(boardGame);
        this.mainLayout = new BorderPane();
        this.boardPane = new GridPane();
        this.playerInfoPanel = new VBox(10);
        this.gameControls = new HBox(10);
        this.diceLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-padding: 0 10 0 0;");
        // Set player names in controller to avoid NullPointerException
        List<String> playerNames = boardGame.getPlayers().stream().map(Player::getName).toList();
        controller.setPlayerNames(playerNames);
        setupUI();
    }

    private void setupUI() {
        // Configure main layout
        mainLayout.setStyle("-fx-background-color: #f0f0f0;");
        mainLayout.setPadding(new Insets(20));

        // Configure board pane
        boardPane.setHgap(2);
        boardPane.setVgap(2);
        boardPane.setPadding(new Insets(10));
        boardPane.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");

        // Configure player info panel
        playerInfoPanel.setPadding(new Insets(10));
        playerInfoPanel.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");
        playerInfoPanel.setPrefWidth(200);

        // Configure game controls
        gameControls.setPadding(new Insets(10));
        gameControls.setAlignment(Pos.CENTER);
        gameControls.setStyle("-fx-background-color: white; -fx-border-color: #cccccc; -fx-border-width: 1;");

        // Add components to main layout
        mainLayout.setCenter(boardPane);
        mainLayout.setRight(playerInfoPanel);
        mainLayout.setBottom(gameControls);

        // Set the root of the existing scene
        getScene().setRoot(mainLayout);

        // Initialize the board
        initializeBoard();

        // Add dice label and roll button to game controls
        rollDiceButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        rollDiceButton.setOnAction(e -> handleRollDice());
        gameControls.getChildren().addAll(diceLabel, rollDiceButton);
    }

    private void initializeBoard() {
        boardPane.getChildren().clear();
        tilePanes.clear();
        int tileIndex = 0;
        // Top row (left to right)
        for (int col = 0; col < GRID_DIM; col++) {
            if (tileIndex >= 20) break;
            Tile tile = getBoardGame().getBoard().getTile(tileIndex);
            StackPane tilePane = createTilePane(tile);
            boardPane.add(tilePane, col, 0);
            tilePanes.put(tileIndex, tilePane);
            tileIndex++;
        }
        // Right column (top to bottom, excluding top)
        for (int row = 1; row < GRID_DIM - 1; row++) {
            if (tileIndex >= 20) break;
            Tile tile = getBoardGame().getBoard().getTile(tileIndex);
            StackPane tilePane = createTilePane(tile);
            boardPane.add(tilePane, GRID_DIM - 1, row);
            tilePanes.put(tileIndex, tilePane);
            tileIndex++;
        }
        // Bottom row (right to left)
        for (int col = GRID_DIM - 1; col >= 0; col--) {
            if (tileIndex >= 20) break;
            Tile tile = getBoardGame().getBoard().getTile(tileIndex);
            StackPane tilePane = createTilePane(tile);
            boardPane.add(tilePane, col, GRID_DIM - 1);
            tilePanes.put(tileIndex, tilePane);
            tileIndex++;
        }
        // Left column (bottom to top, excluding top and bottom)
        for (int row = GRID_DIM - 2; row > 0; row--) {
            if (tileIndex >= 20) break;
            Tile tile = getBoardGame().getBoard().getTile(tileIndex);
            StackPane tilePane = createTilePane(tile);
            boardPane.add(tilePane, 0, row);
            tilePanes.put(tileIndex, tilePane);
            tileIndex++;
        }
        // Fill the center with blank tiles
        for (int row = 1; row < GRID_DIM - 1; row++) {
            for (int col = 1; col < GRID_DIM - 1; col++) {
                Rectangle blank = new Rectangle(70, 70, BLANK_COLOR);
                StackPane blankPane = new StackPane(blank);
                boardPane.add(blankPane, col, row);
            }
        }
        updatePlayerInfoPanel();
        updatePlayerTokens();
        updateRollDiceButtonState();
    }

    private StackPane createTilePane(Tile tile) {
        Rectangle rect = new Rectangle(70, 70);
        rect.setArcWidth(10);
        rect.setArcHeight(10);
        Label label = new Label();
        label.setWrapText(true);
        label.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        if (tile instanceof PropertyTile) {
            PropertyTile pt = (PropertyTile) tile;
            rect.setFill(GROUP_COLORS[pt.getGroup() % GROUP_COLORS.length]);
            label.setText("Property\n$" + pt.getPrice());
        } else if (tile instanceof GoTile) {
            rect.setFill(GO_COLOR);
            label.setText("GO");
        } else if (tile instanceof JailTile) {
            rect.setFill(JAIL_COLOR);
            label.setText("JAIL");
        } else if (tile instanceof FreeParkingTile) {
            rect.setFill(FREE_PARKING_COLOR);
            label.setText("FREE\nPARKING");
        } else {
            rect.setFill(BLANK_COLOR);
            label.setText("");
        }
        StackPane pane = new StackPane(rect, label);
        pane.setPrefSize(70, 70);
        return pane;
    }

    private void updatePlayerInfoPanel() {
        playerInfoPanel.getChildren().clear();
        for (Player player : getBoardGame().getPlayers()) {
            VBox card = new VBox(5);
            card.setPadding(new Insets(10));
            card.setStyle("-fx-background-color: #f8f8f8; -fx-border-color: #cccccc; -fx-border-radius: 5; -fx-background-radius: 5;");
            Label nameLabel = new Label(player.getName());
            nameLabel.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");
            if (player == getBoardGame().getCurrentPlayer()) {
                nameLabel.setTextFill(Color.DARKBLUE);
                card.setStyle("-fx-background-color: #e0f0ff; -fx-border-color: #0077cc; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");
            }
            int money = ((SimpleMonopolyPlayer) player).getMoney();
            Label moneyLabel = new Label("Money: $" + money);
            List<PropertyTile> props = ((SimpleMonopolyPlayer) player).getOwnedProperties();
            String propList = props.isEmpty() ? "None" : String.join(", ", props.stream().map(p -> "#" + p.getId()).toList());
            Label propLabel = new Label("Properties: " + propList);
            card.getChildren().addAll(nameLabel, moneyLabel, propLabel);
            playerInfoPanel.getChildren().add(card);
        }
    }

    private void updatePlayerTokens() {
        // Remove all tokens
        for (StackPane pane : tilePanes.values()) {
            pane.getChildren().removeIf(n -> n instanceof Circle);
        }
        // Add tokens for each player
        int colorIdx = 0;
        Color[] tokenColors = { Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PURPLE };
        for (Player player : getBoardGame().getPlayers()) {
            int pos = player.getCurrentTile() != null ? player.getCurrentTile().getId() : 0;
            StackPane tilePane = tilePanes.get(pos);
            Circle token = new Circle(12, tokenColors[colorIdx % tokenColors.length]);
            token.setStroke(Color.BLACK);
            tilePane.getChildren().add(token);
            colorIdx++;
        }
    }

    private void updateRollDiceButtonState() {
        Player current = getBoardGame().getCurrentPlayer();
        boolean isGameOver = getBoardGame().isGameOver();
        rollDiceButton.setDisable(current == null || isGameOver);
    }

    private void handleRollDice() {
        controller.handlePlayerMove();
        // Show dice value
        int[] diceValues = getBoardGame().getCurrentDiceValues();
        if (diceValues != null && diceValues.length > 0) {
            diceLabel.setText("Dice: " + Arrays.toString(diceValues));
        } else {
            diceLabel.setText("Dice: -");
        }
        updateUI();
    }

    private void updateUI() {
        Platform.runLater(() -> {
            updatePlayerInfoPanel();
            updatePlayerTokens();
            updateRollDiceButtonState();
            // Update dice label for current player
            int[] diceValues = getBoardGame().getCurrentDiceValues();
            if (diceValues != null && diceValues.length > 0) {
                diceLabel.setText("Dice: " + Arrays.toString(diceValues));
            } else {
                diceLabel.setText("Dice: -");
            }
        });
    }

    @Override
    public void refreshUIFromBoardGame() {
        super.refreshUIFromBoardGame();
        updateUI();
    }

    @Override
    public Scene getScene() {
        return super.getScene();
    }
} 