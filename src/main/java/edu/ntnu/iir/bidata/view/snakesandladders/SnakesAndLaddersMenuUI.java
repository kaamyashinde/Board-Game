package edu.ntnu.iir.bidata.view.snakesandladders;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import edu.ntnu.iir.bidata.view.common.BoardManagementUI;
import edu.ntnu.iir.bidata.view.common.PlayerSelectionUI;
import edu.ntnu.iir.bidata.view.common.MainMenuUI;
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import edu.ntnu.iir.bidata.controller.SnakesAndLaddersController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.Player;
import edu.ntnu.iir.bidata.filehandling.boardgame.BoardGameFileReaderGson;
import lombok.Getter;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SnakesAndLaddersMenuUI {
    private final Stage primaryStage;
    private final Consumer<List<String>> onStartGame;
    private final BoardManagementUI boardManagementUI;
    /**
     * -- GETTER --
     *  Get the list of selected players
     *
     */
    @Getter
    private List<String> selectedPlayers = new ArrayList<>();
    private Label playerCountLabel;
    private static final Logger LOGGER = Logger.getLogger(SnakesAndLaddersMenuUI.class.getName());

    /**
     * Creates a new Snakes and Ladders Menu UI
     *
     * @param primaryStage The primary stage
     * @param onStartGame Consumer that accepts the list of selected players when starting the game
     */
    public SnakesAndLaddersMenuUI(Stage primaryStage, Consumer<List<String>> onStartGame) {
        this.primaryStage = primaryStage;
        this.onStartGame = onStartGame;
        this.boardManagementUI = new BoardManagementUI(primaryStage);
        setupMenu();
    }

    private void setupMenu() {
        primaryStage.setTitle("Snakes & Ladders");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.getStyleClass().add("snl-menu-root");

        // Create top bar with back button
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);

        Button backButton = new Button("← Back to Main Menu");
        backButton.getStyleClass().add("snl-back-button");
        backButton.setOnAction(e -> {
            JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage);
        });

        topBar.getChildren().add(backButton);
        root.setTop(topBar);

        VBox logoStack = createLogoStack();
        root.setLeft(logoStack);

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(40, 0, 0, 0));

        StackPane titlePane = new StackPane();
        titlePane.setPrefSize(400, 60);
        titlePane.getStyleClass().add("snl-title-pane");
        Label titleLabel = new Label("SNAKES & LADDERS");
        titleLabel.getStyleClass().add("snl-title-label");
        titlePane.getChildren().add(titleLabel);
        centerBox.getChildren().add(titlePane);

        HBox boardButtons = new HBox(30);
        boardButtons.setAlignment(Pos.CENTER);
        Button loadBoardBtn = createMenuButton("LOAD BOARD");
        loadBoardBtn.setOnAction(e -> showLoadBoardDialog());
        boardButtons.getChildren().add(loadBoardBtn);
        centerBox.getChildren().add(boardButtons);

        // Choose The Players button
        Button choosePlayersBtn = createMenuButton("Choose The Players");
        choosePlayersBtn.setOnAction(e -> openPlayerSelection());
        centerBox.getChildren().add(choosePlayersBtn);

        // Player count label
        playerCountLabel = new Label("No players selected");
        playerCountLabel.getStyleClass().add("snl-player-count-label");
        centerBox.getChildren().add(playerCountLabel);

        // START button
        Button startGameBtn = createMenuButton("START");
        startGameBtn.setOnAction(e -> {
            if (selectedPlayers.size() >= 1) {
                if (onStartGame != null) onStartGame.accept(selectedPlayers);
            } else {
                playerCountLabel.setText("Please select at least one player!");
                playerCountLabel.setStyle("-fx-text-fill: red;");
            }
        });
        centerBox.getChildren().add(startGameBtn);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 1200, 800);
        scene.getStylesheets().addAll(
            getClass().getResource("/styles.css").toExternalForm(),
            getClass().getResource("/snakesandladders.css").toExternalForm()
        );
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLogoStack() {
        VBox logoStack = new VBox(8);
        logoStack.setPadding(new Insets(10, 20, 10, 10));
        logoStack.setAlignment(Pos.TOP_LEFT);
        Color[] greens = {
            Color.web("#006400"), Color.web("#008000"), Color.web("#00A000"),
            Color.web("#4caf50"), Color.web("#bdebc8")
        };
        int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
        for (int i = 0; i < 11; i++) {
            Region r = new Region();
            r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
            r.setStyle("-fx-background-radius: 15; -fx-background-color: " + toHexString(greens[i % greens.length]) + ";");
            logoStack.getChildren().add(r);
        }
        return logoStack;
    }

    private void openPlayerSelection() {
        PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
        List<String> players = playerSelection.showAndWait();

        if (players != null && !players.isEmpty()) {
            this.selectedPlayers = players;
            updatePlayerCountLabel();
        }
    }

    private void updatePlayerCountLabel() {
        if (selectedPlayers.isEmpty()) {
            playerCountLabel.setText("No players selected");
        } else {
            playerCountLabel.setText(selectedPlayers.size() + " player(s) selected");
            playerCountLabel.getStyleClass().add("snl-player-count-label");
        }
    }

    private String toHexString(Color color) {
        return String.format("#%02X%02X%02X",
            (int) (color.getRed() * 255),
            (int) (color.getGreen() * 255),
            (int) (color.getBlue() * 255));
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(200);
        button.setPrefHeight(50);
        button.getStyleClass().add("snl-menu-button");
        return button;
    }

    private void showLoadBoardDialog() {
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Load Snakes and Ladders Game");
        dialog.setHeaderText("Select a saved game to load");

        javafx.scene.control.ComboBox<String> gameList = new javafx.scene.control.ComboBox<>();
        gameList.setPromptText("Select a game");
        java.io.File savedGamesDir = new java.io.File("src/main/resources/saved_games/snakesandladders");
        final long MAX_SIZE = 1024 * 1024; // 1MB
        if (savedGamesDir.exists() && savedGamesDir.isDirectory()) {
            java.io.File[] files = savedGamesDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (java.io.File file : files) {
                    try {
                        if (file.length() > MAX_SIZE) continue; // Skip large files
                        gameList.getItems().add(file.getName().replace(".json", ""));
                    } catch (Exception e) {
                        // Skip files that can't be read
                        continue;
                    }
                }
            }
        }

        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(10);
        content.getChildren().addAll(new javafx.scene.control.Label("Select a saved game:"), gameList);
        dialog.getDialogPane().setContent(content);

        javafx.scene.control.ButtonType loadButtonType = new javafx.scene.control.ButtonType("Load", javafx.scene.control.ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loadButtonType, javafx.scene.control.ButtonType.CANCEL);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loadButtonType) {
                return gameList.getValue();
            }
            return null;
        });

        dialog.showAndWait().ifPresent(gameName -> {
            if (gameName != null && !gameName.isEmpty()) {
                try {
                    // Create controller and load game
                    BoardGameFileReaderGson reader = new BoardGameFileReaderGson();
                    BoardGame boardGame = reader.readBoardGame(Paths.get("src/main/resources/saved_games/snakesandladders", gameName + ".json"));
                    List<Player> players = boardGame.getPlayers();
                    
                    // Create view and controller
                    SnakesAndLaddersGameUI gameUI = new SnakesAndLaddersGameUI(primaryStage, players);
                    SnakesAndLaddersController controller = new SnakesAndLaddersController(boardGame);
                    gameUI.setController(controller);
                    gameUI.setBoardGame(boardGame);
                    
                    // Register UI as observer
                    boardGame.addObserver(gameUI);
                    
                    // Load game state and start
                    controller.loadGame(gameName, gameUI);
                    controller.startGame();
                    
                    // Create and set the scene
                    Scene scene = new Scene(gameUI.getRoot(), 1200, 800);
                    scene.getStylesheets().addAll(
                        getClass().getResource("/styles.css").toExternalForm(),
                        getClass().getResource("/snakesandladders.css").toExternalForm()
                    );
                    primaryStage.setScene(scene);
                    primaryStage.show();
                } catch (Exception e) {
                    LOGGER.log(Level.SEVERE, "Error loading Snakes and Ladders game", e);
                }
            }
        });
    }
}