package edu.ntnu.iir.bidata.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * MainMenuUI class for the main menu of the game.
 * Pure frontend implementation without backend logic.
 */
public class MainMenuUI {
  private final Stage primaryStage;
  private final Runnable onSnakesAndLaddersSelected;
  private final Runnable onLudoSelected;

  /**
   * Creates a new Main Menu UI
   *
   * @param primaryStage The primary stage
   * @param onSnakesAndLaddersSelected Callback for when Snakes and Ladders is selected
   * @param onLudoSelected Callback for when Ludo is selected
   */
  public MainMenuUI(Stage primaryStage, Runnable onSnakesAndLaddersSelected, Runnable onLudoSelected) {
    this.primaryStage = primaryStage;
    this.onSnakesAndLaddersSelected = onSnakesAndLaddersSelected;
    this.onLudoSelected = onLudoSelected;
    setupMainMenu();
  }

  private void setupMainMenu() {
    primaryStage.setTitle("Game Selection");

    BorderPane root = new BorderPane();
    root.setPadding(new Insets(20));
    root.setStyle("-fx-background-color: #f5fff5;");

    // --- LEFT: PURPLE LOGO STRIP (WITHOUT TEXT) ---
    Color[] purples = {
        Color.web("#2d0066"), Color.web("#4b0082"), Color.web("#6a0dad"),
        Color.web("#7c3aed"), Color.web("#a084e8"), Color.web("#b39ddb"), Color.web("#c3aed6")
    };
    int[] purpleHeights = {40, 60, 40, 30, 20, 40, 30, 20, 40, 30, 20};

    // Create the left logo pane WITHOUT text and WITHOUT clickable functionality
    StackPane leftLogo = createLogoStackPane(purples, purpleHeights, "", null);
    root.setLeft(leftLogo);

    // --- CENTER: "WELCOME" + two rounded rectangles ---
    VBox centerBox = new VBox(30);
    centerBox.setAlignment(Pos.TOP_CENTER);
    centerBox.setPadding(new Insets(40, 0, 0, 0));

    // WELCOME banner
    StackPane welcomePane = new StackPane();
    welcomePane.setPrefSize(400, 60);
    welcomePane.setStyle("-fx-background-color: #bfc2fa; -fx-background-radius: 20;");
    Label welcomeLabel = new Label("WELCOME");
    welcomeLabel.setStyle("-fx-font-size: 30px; -fx-font-family: serif; -fx-font-weight: bold;");
    welcomePane.getChildren().add(welcomeLabel);

    // Game selection boxes
    HBox menuRow = new HBox(40);
    menuRow.setAlignment(Pos.CENTER);

    // Snakes & Ladders game box
    StackPane snakesAndLaddersPane = createGamePane(
        "#c2c2fa",
        "#2e8b57",
        "Snakes & Ladders",
        createSnakesAndLaddersGrid(),
        onSnakesAndLaddersSelected
    );

    // Ludo game box
    StackPane ludoPane = createGamePane(
        "#c2c2fa",
        "#e69a28",
        "Ludo",
        createLudoGrid(),
        onLudoSelected
    );

    menuRow.getChildren().addAll(snakesAndLaddersPane, ludoPane);
    centerBox.getChildren().addAll(welcomePane, menuRow);
    root.setCenter(centerBox);

    // --- BOTTOM-RIGHT: credits ---
    Label credit = new Label("©: Durva Parmar & Kamya Shinde");
    credit.setStyle(
        "-fx-font-size: 10px; " +
            "-fx-background-color: #ecebff; " +
            "-fx-background-radius: 10; " +
            "-fx-padding: 2 8 2 8;"
    );
    StackPane creditPane = new StackPane(credit);
    creditPane.setAlignment(Pos.BOTTOM_RIGHT);
    root.setBottom(creditPane);

    Scene scene = new Scene(root, 900, 700);
    primaryStage.setScene(scene);
    primaryStage.show();
  }

  /**
   * Creates a game selection pane with a grid and label
   */
  private StackPane createGamePane(String bgColor, String borderColor, String gameName, Region gameGrid, Runnable onClick) {
    StackPane gamePane = new StackPane();
    gamePane.setPrefSize(220, 200);
    gamePane.setStyle(
        "-fx-background-color: " + bgColor + "; " +
            "-fx-background-radius: 18; " +
            "-fx-border-color: " + borderColor + "; " +
            "-fx-border-width: 3; " +
            "-fx-border-radius: 18;"
    );

    Label gameLabel = new Label(gameName);
    gameLabel.setStyle(
        "-fx-font-size: 18px; " +
            "-fx-font-weight: bold; " +
            "-fx-text-fill: white; " +
            "-fx-background-color: rgba(0, 100, 0, 0.7); " +
            "-fx-padding: 5 10; " +
            "-fx-background-radius: 10;"
    );

    StackPane gameContent = new StackPane(gameGrid, gameLabel);
    gamePane.getChildren().add(gameContent);

    // Make the box clickable
    if (onClick != null) {
      gamePane.setOnMouseClicked(e -> onClick.run());
      gamePane.setCursor(Cursor.HAND);
    }

    return gamePane;
  }

  /**
   * Creates a Snakes & Ladders grid for visualization
   */
  private GridPane createSnakesAndLaddersGrid() {
    GridPane boardGrid = new GridPane();
    int size = 6;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        Region sq = new Region();
        sq.setPrefSize(20, 20);
        sq.setStyle("-fx-background-color: " + (((i + j) % 2 == 0) ? "#e0ffe0" : "#7ed957") + ";");
        boardGrid.add(sq, j, i);
      }
    }
    return boardGrid;
  }

  /**
   * Creates a Ludo grid for visualization
   */
  private GridPane createLudoGrid() {
    GridPane ludoGrid = new GridPane();

    // Create a simplified Ludo board representation
    Color[] playerColors = {
        Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW
    };

    int size = 5;
    for (int i = 0; i < size; i++) {
      for (int j = 0; j < size; j++) {
        Region sq = new Region();
        sq.setPrefSize(20, 20);

        // Corner regions are player bases
        if ((i == 0 && j == 0) || (i == 0 && j == size-1) ||
            (i == size-1 && j == 0) || (i == size-1 && j == size-1)) {
          int colorIndex = 0;
          if (i == 0 && j == 0) colorIndex = 0;
          else if (i == 0 && j == size-1) colorIndex = 1;
          else if (i == size-1 && j == 0) colorIndex = 2;
          else colorIndex = 3;

          sq.setStyle("-fx-background-color: " + toHexString(playerColors[colorIndex]) + ";");
        } else if (i == size/2 && j == size/2) {
          // Center square
          sq.setStyle("-fx-background-color: white;");
        } else {
          // Path squares
          sq.setStyle("-fx-background-color: " + (((i + j) % 2 == 0) ? "#fff0f0" : "#f0f0ff") + ";");
        }
        ludoGrid.add(sq, j, i);
      }
    }

    return ludoGrid;
  }

  /**
   * Creates the left‐hand "logo" strip: a vertical stack of colored Regions
   * plus a centered label. If onClick is non‐null, the whole strip is clickable.
   * If text is empty, no label will be displayed.
   */
  private StackPane createLogoStackPane(Color[] colors, int[] heights, String text, Runnable onClick) {
    VBox stack = new VBox(8);
    stack.setPadding(new Insets(10, 20, 10, 10));
    stack.setAlignment(Pos.TOP_LEFT);

    // Only add the title container if text is not empty
    if (text != null && !text.isEmpty()) {
      // Create a container for the title at the top
      VBox titleContainer = new VBox();
      titleContainer.setAlignment(Pos.CENTER);
      titleContainer.setPadding(new Insets(0, 0, 10, 0));

      // Create the title label
      Label titleLabel = new Label(text);
      titleLabel.setStyle(
          "-fx-text-fill: white; " +
              "-fx-font-size: 22px; " +
              "-fx-font-weight: bold; " +
              "-fx-effect: dropshadow(gaussian, #000, 2, 0, 1, 1);"
      );
      titleContainer.getChildren().add(titleLabel);

      // Add title to the top of our stack
      stack.getChildren().add(titleContainer);
    }

    // Then add colored regions
    for (int i = 0; i < heights.length; i++) {
      Region r = new Region();
      int w = (i % 3 == 0 ? 40 : (i % 3 == 1 ? 30 : 60));
      r.setPrefSize(w, heights[i]);
      r.setStyle(
          "-fx-background-radius: 15; " +
              "-fx-background-color: " + toHexString(colors[i % colors.length]) + ";"
      );
      stack.getChildren().add(r);
    }

    StackPane pane = new StackPane(stack);
    pane.setPrefWidth(180);
    pane.setAlignment(Pos.CENTER);

    // Make the entire pane clickable if onClick is provided
    if (onClick != null) {
      pane.setOnMouseClicked(e -> onClick.run());
      pane.setCursor(Cursor.HAND);
    }

    return pane;
  }

  /**
   * Converts a JavaFX Color to a hex string
   */
  private String toHexString(Color c) {
    return String.format("#%02X%02X%02X",
        (int)(c.getRed() * 255),
        (int)(c.getGreen() * 255),
        (int)(c.getBlue() * 255)
    );
  }
}