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
import edu.ntnu.iir.bidata.model.tile.actions.monopoly.GoToJailAction;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import java.io.File;
import java.util.*;
import java.util.logging.Logger;
import javafx.stage.Stage;

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
    private final Button buyButton = new Button("Buy");
    private final Button skipButton = new Button("Skip");
    private final Button payRentButton = new Button("Pay Rent");
    private final Button jailRollButton = new Button("Roll Dice (Jail)");
    private final Button jailPayButton = new Button("Pay $50");
    private final Button saveButton = new Button("Save Game");
    private final Button loadButton = new Button("Load Game");
    private final Button backButton = new Button("Back to Main Menu");
    private final Label actionLabel = new Label("");
    private final MonopolyController controller;
    private final Color BLANK_COLOR = Color.LIGHTGRAY;
    private final Color[] GROUP_COLORS = { Color.SADDLEBROWN, Color.LIGHTBLUE, Color.HOTPINK, Color.ORANGE };
    private final Color GO_COLOR = Color.LIMEGREEN;
    private final Color JAIL_COLOR = Color.DARKGRAY;
    private final Color FREE_PARKING_COLOR = Color.GOLD;
    private final Color GO_TO_JAIL_COLOR = Color.ORANGERED;
    private final BorderPane mainLayout;
    private final GridPane boardPane;
    private final VBox playerInfoPanel;
    private final HBox gameControls;
    private final Label diceLabel = new Label("Dice: -");
    protected BoardGame boardGame;
    private final Stage primaryStage;

    public MonopolyGameUI(BoardGame boardGame, Stage primaryStage) {
        super(boardGame);
        this.boardGame = boardGame;
        this.primaryStage = primaryStage;
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
        buyButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #2196F3; -fx-text-fill: white;");
        skipButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #FF9800; -fx-text-fill: white;");
        payRentButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #E53935; -fx-text-fill: white;");
        jailRollButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #4CAF50; -fx-text-fill: white;");
        jailPayButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #FFD600; -fx-text-fill: black;");
        backButton.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #f7e6c7; -fx-text-fill: #3b3b6d;");
        buyButton.setOnAction(e -> handleBuyProperty());
        skipButton.setOnAction(e -> handleSkipAction());
        payRentButton.setOnAction(e -> handlePayRent());
        jailRollButton.setOnAction(e -> handleJailRoll());
        jailPayButton.setOnAction(e -> handleJailPay());
        backButton.setOnAction(e -> handleBackToMainMenu());
        actionLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #333;");
        saveButton.getStyleClass().add("game-control-button");
        loadButton.getStyleClass().add("game-control-button");
        
        saveButton.setOnAction(e -> {
            if (controller != null) {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Save Game");
                dialog.setHeaderText("Enter a name for your saved game");
                dialog.setContentText("Game name:");

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(gameName -> {
                    try {
                        controller.saveGame(gameName);
                        actionLabel.setText("Game saved as: " + gameName);
                    } catch (Exception ex) {
                        actionLabel.setText("Error saving game: " + ex.getMessage());
                    }
                });
            }
        });

        loadButton.setOnAction(e -> {
            if (controller != null) {
                Dialog<String> dialog = new Dialog<>();
                dialog.setTitle("Load Game");
                dialog.setHeaderText("Select a saved game to load");

                ComboBox<String> gameList = new ComboBox<>();
                gameList.setPromptText("Select a game");
                
                File savedGamesDir = new File("src/main/resources/saved_games");
                if (savedGamesDir.exists() && savedGamesDir.isDirectory()) {
                    File[] savedGames = savedGamesDir.listFiles((dir, name) -> name.endsWith(".json"));
                    if (savedGames != null) {
                        for (File game : savedGames) {
                            String gameName = game.getName().replace(".json", "");
                            gameList.getItems().add(gameName);
                        }
                    }
                }

                dialog.getDialogPane().setContent(gameList);
                ButtonType loadButtonType = new ButtonType("Load", ButtonBar.ButtonData.OK_DONE);
                dialog.getDialogPane().getButtonTypes().addAll(loadButtonType, ButtonType.CANCEL);

                dialog.setResultConverter(dialogButton -> {
                    if (dialogButton == loadButtonType) {
                        return gameList.getValue();
                    }
                    return null;
                });

                Optional<String> result = dialog.showAndWait();
                result.ifPresent(gameName -> {
                    try {
                        controller.loadGame(gameName, this);
                        actionLabel.setText("Game loaded: " + gameName);
                    } catch (Exception ex) {
                        actionLabel.setText("Error loading game: " + ex.getMessage());
                    }
                });
            }
        });

        gameControls.getChildren().add(backButton);
        gameControls.getChildren().addAll(rollDiceButton, buyButton, skipButton, payRentButton, 
            jailRollButton, jailPayButton, saveButton, loadButton, diceLabel);
        buyButton.setVisible(false);
        skipButton.setVisible(false);
        payRentButton.setVisible(false);
        jailRollButton.setVisible(false);
        jailPayButton.setVisible(false);
        actionLabel.setVisible(false);
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
        } else if (tile.getAction() instanceof GoToJailAction) {
            rect.setFill(GO_TO_JAIL_COLOR);
            label.setText("GO TO\nJAIL");
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
        boolean awaitingBuy = controller.isAwaitingPlayerAction();
        boolean awaitingRent = controller.isAwaitingRentAction();
        boolean inJail = controller.isCurrentPlayerInJail();
        rollDiceButton.setDisable(current == null || isGameOver || awaitingBuy || awaitingRent || inJail);
        buyButton.setDisable(isGameOver || awaitingRent || inJail);
        skipButton.setDisable(isGameOver || awaitingRent || inJail);
        payRentButton.setDisable(isGameOver || awaitingBuy || inJail);
        jailRollButton.setDisable(isGameOver || !inJail);
        jailPayButton.setDisable(isGameOver || !inJail);
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

    private void handleBuyProperty() {
        controller.buyPropertyForCurrentPlayer();
        updateUI();
    }

    private void handleSkipAction() {
        controller.skipActionForCurrentPlayer();
        updateUI();
    }

    private void handlePayRent() {
        controller.payRentForCurrentPlayer();
        updateUI();
    }

    private void handleJailRoll() {
        controller.handleJailRollDice();
        updateUI();
    }

    private void handleJailPay() {
        controller.handleJailPay();
        updateUI();
    }

    private void handleBackToMainMenu() {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.CONFIRMATION);
        alert.setTitle("Exit Monopoly");
        alert.setHeaderText("Do you want to save your game before exiting?");
        javafx.scene.control.ButtonType saveAndExit = new javafx.scene.control.ButtonType("Save and Exit");
        javafx.scene.control.ButtonType exitWithoutSaving = new javafx.scene.control.ButtonType("Exit without Saving");
        javafx.scene.control.ButtonType cancel = new javafx.scene.control.ButtonType("Cancel", javafx.scene.control.ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveAndExit, exitWithoutSaving, cancel);
        java.util.Optional<javafx.scene.control.ButtonType> result = alert.showAndWait();
        if (result.isPresent()) {
            if (result.get() == saveAndExit) {
                javafx.scene.control.TextInputDialog dialog = new javafx.scene.control.TextInputDialog();
                dialog.setTitle("Save Game");
                dialog.setHeaderText("Enter a name for your saved game:");
                java.util.Optional<String> saveName = dialog.showAndWait();
                saveName.ifPresent(name -> {
                    controller.saveGame(name);
                    edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
                });
            } else if (result.get() == exitWithoutSaving) {
                edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
            }
            // If cancel, do nothing
        }
    }

    private void updateUI() {
        Platform.runLater(() -> {
            updatePlayerInfoPanel();
            updatePlayerTokens();
            updateRollDiceButtonState();
            int[] diceValues = getBoardGame().getCurrentDiceValues();
            if (diceValues != null && diceValues.length > 0) {
                diceLabel.setText("Dice: " + Arrays.toString(diceValues));
            } else {
                diceLabel.setText("Dice: -");
            }
            if (controller.isCurrentPlayerInJail()) {
                actionLabel.setText("You are in jail! Roll a 6 or pay $50 to get out.");
                actionLabel.setVisible(true);
                jailRollButton.setVisible(true);
                jailPayButton.setVisible(true);
                buyButton.setVisible(false);
                skipButton.setVisible(false);
                payRentButton.setVisible(false);
            } else if (controller.isAwaitingPlayerAction()) {
                PropertyTile prop = controller.getPendingPropertyTile();
                actionLabel.setText("Buy property for $" + (prop != null ? prop.getPrice() : "?") + "?");
                actionLabel.setVisible(true);
                buyButton.setVisible(true);
                skipButton.setVisible(true);
                payRentButton.setVisible(false);
                jailRollButton.setVisible(false);
                jailPayButton.setVisible(false);
            } else if (controller.isAwaitingRentAction()) {
                PropertyTile prop = controller.getPendingRentPropertyTile();
                int rent = prop != null ? prop.getRent() : 0;
                actionLabel.setText("Pay rent: $" + rent);
                actionLabel.setVisible(true);
                buyButton.setVisible(false);
                skipButton.setVisible(false);
                payRentButton.setVisible(true);
                jailRollButton.setVisible(false);
                jailPayButton.setVisible(false);
            } else {
                actionLabel.setVisible(false);
                buyButton.setVisible(false);
                skipButton.setVisible(false);
                payRentButton.setVisible(false);
                jailRollButton.setVisible(false);
                jailPayButton.setVisible(false);
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

    public void setBoardGame(BoardGame boardGame) {
        this.boardGame = boardGame;
        initializeBoard();
        updateUI();
    }
} 