package edu.ntnu.iir.bidata.view.monopoly;

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
import edu.ntnu.iir.bidata.view.common.JavaFXBoardGameLauncher;
import lombok.Getter;

public class MonopolyMenuUI {
    private final Stage primaryStage;
    private final Consumer<List<String>> onStartGame;
    private final BoardManagementUI boardManagementUI;
    @Getter
    private List<String> selectedPlayers = new ArrayList<>();
    private Label playerCountLabel;

    public MonopolyMenuUI(Stage primaryStage, Consumer<List<String>> onStartGame) {
        this.primaryStage = primaryStage;
        this.onStartGame = onStartGame;
        this.boardManagementUI = new BoardManagementUI(primaryStage);
        setupMenu();
    }

    private void setupMenu() {
        primaryStage.setTitle("Monopoly");

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(20));
        root.setStyle("-fx-background-color: #f7f7f0;");

        // Top bar with back button
        HBox topBar = new HBox(10);
        topBar.setPadding(new Insets(10));
        topBar.setAlignment(Pos.CENTER_LEFT);
        Button backButton = new Button("← Back to Main Menu");
        backButton.setStyle("-fx-background-color: #f7e6c7; -fx-font-weight: bold;");
        backButton.setOnAction(e -> JavaFXBoardGameLauncher.getInstance().showMainMenu(primaryStage));
        topBar.getChildren().add(backButton);
        root.setTop(topBar);

        VBox logoStack = createLogoStack();
        root.setLeft(logoStack);

        VBox centerBox = new VBox(30);
        centerBox.setAlignment(Pos.TOP_CENTER);
        centerBox.setPadding(new Insets(40, 0, 0, 0));

        StackPane titlePane = new StackPane();
        titlePane.setPrefSize(400, 60);
        titlePane.setStyle("-fx-background-color: #f7e6c7; -fx-background-radius: 20;");
        Label titleLabel = new Label("MONOPOLY");
        titleLabel.setStyle("-fx-font-size: 30px; -fx-font-family: serif; -fx-font-weight: bold;");
        titlePane.getChildren().add(titleLabel);
        centerBox.getChildren().add(titlePane);

        HBox boardButtons = new HBox(30);
        boardButtons.setAlignment(Pos.CENTER);
        Button loadBoardBtn = createMenuButton("LOAD BOARD");
        loadBoardBtn.setOnAction(e -> showLoadBoardDialog());
        boardButtons.getChildren().add(loadBoardBtn);
        centerBox.getChildren().add(boardButtons);

        Button choosePlayersBtn = createMenuButton("Choose The Players");
        choosePlayersBtn.setOnAction(e -> openPlayerSelection());
        centerBox.getChildren().add(choosePlayersBtn);

        playerCountLabel = new Label("No players selected");
        playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b3b6d;");
        centerBox.getChildren().add(playerCountLabel);

        Button startGameBtn = createMenuButton("START");
        startGameBtn.setOnAction(e -> {
            if (selectedPlayers.size() >= 2) {
                if (onStartGame != null) onStartGame.accept(selectedPlayers);
            } else {
                playerCountLabel.setText("Please select at least two players!");
                playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: red;");
            }
        });
        centerBox.getChildren().add(startGameBtn);

        root.setCenter(centerBox);

        Scene scene = new Scene(root, 900, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox createLogoStack() {
        VBox logoStack = new VBox(8);
        logoStack.setPadding(new Insets(10, 20, 10, 10));
        logoStack.setAlignment(Pos.TOP_LEFT);
        Color[] colors = {
            Color.web("#3b3b6d"), Color.web("#f7e6c7"), Color.web("#b39ddb"),
            Color.web("#e69a28"), Color.web("#c2c2fa")
        };
        int[] heights = {40, 30, 40, 20, 30, 20, 40, 30, 20, 40, 30};
        for (int i = 0; i < 11; i++) {
            Region r = new Region();
            r.setPrefSize((i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60)), heights[i]);
            r.setStyle("-fx-background-radius: 15; -fx-background-color: " + toHexString(colors[i % colors.length]) + ";");
            logoStack.getChildren().add(r);
        }
        return logoStack;
    }

    private Button createMenuButton(String text) {
        Button btn = new Button(text);
        btn.setStyle("-fx-background-color: #f7e6c7; -fx-font-size: 18px; -fx-font-weight: bold; -fx-background-radius: 20; -fx-padding: 10 40; -fx-text-fill: #3b3b6d;");
        return btn;
    }

    private void openPlayerSelection() {
        PlayerSelectionUI playerSelection = new PlayerSelectionUI(primaryStage);
        List<String> playerNames = playerSelection.showAndWait();
        if (playerNames != null && !playerNames.isEmpty()) {
            selectedPlayers = playerNames;
            playerCountLabel.setText(selectedPlayers.size() + " players selected");
            playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b3b6d;");
        } else {
            playerCountLabel.setText("No players selected");
            playerCountLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #3b3b6d;");
        }
    }

    private String toHexString(Color c) {
        return String.format("#%02X%02X%02X",
            (int)(c.getRed() * 255),
            (int)(c.getGreen() * 255),
            (int)(c.getBlue() * 255)
        );
    }

    private void showLoadBoardDialog() {
        javafx.scene.control.Dialog<String> dialog = new javafx.scene.control.Dialog<>();
        dialog.setTitle("Load Monopoly Game");
        dialog.setHeaderText("Select a saved Monopoly game to load");

        javafx.scene.control.ComboBox<String> gameList = new javafx.scene.control.ComboBox<>();
        gameList.setPromptText("Select a game");
        java.io.File savedGamesDir = new java.io.File("src/main/resources/saved_games");
        final long MAX_SIZE = 1024 * 1024; // 1MB
        if (savedGamesDir.exists() && savedGamesDir.isDirectory()) {
            java.io.File[] files = savedGamesDir.listFiles((dir, name) -> name.endsWith(".json"));
            if (files != null) {
                for (java.io.File file : files) {
                    try {
                        if (file.length() > MAX_SIZE) continue; // Skip large files
                        String json = java.nio.file.Files.readString(file.toPath());
                        com.google.gson.Gson gson = new com.google.gson.Gson();
                        edu.ntnu.iir.bidata.model.gamestate.MonopolyGameState gameState = gson.fromJson(json, edu.ntnu.iir.bidata.model.gamestate.MonopolyGameState.class);
                        if ("MONOPOLY".equals(gameState.getGameType())) {
                            gameList.getItems().add(file.getName().replace(".json", ""));
                        }
                    } catch (OutOfMemoryError oom) {
                        // Skip files that are too large or cause OOM
                        continue;
                    } catch (Exception e) {
                        // Skip files that can't be read or aren't valid Monopoly saves
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
                JavaFXBoardGameLauncher.getInstance().showMonopolyGameBoardWithLoad(primaryStage, gameName);
            }
        });
    }
} 