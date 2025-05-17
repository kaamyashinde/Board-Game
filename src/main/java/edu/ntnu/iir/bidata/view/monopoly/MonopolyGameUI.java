package edu.ntnu.iir.bidata.view.monopoly;

import edu.ntnu.iir.bidata.controller.MonopolyController;
import edu.ntnu.iir.bidata.model.BoardGame;
import edu.ntnu.iir.bidata.model.player.SimpleMonopolyPlayer;
import edu.ntnu.iir.bidata.model.tile.core.Tile;
import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import edu.ntnu.iir.bidata.view.common.JavaFXGameUI;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import java.util.Map;
import java.util.logging.Logger;

/**
 * JavaFX UI implementation for the Monopoly game.
 */
public class MonopolyGameUI extends JavaFXGameUI {
    private static final Logger LOGGER = Logger.getLogger(MonopolyGameUI.class.getName());
    private MonopolyController controller;
    private VBox propertyInfoPane;
    private Label moneyLabel;
    private Label propertiesLabel;
    private Button buyPropertyButton;
    private Button buildHouseButton;
    private Button mortgageButton;
    private Scene scene;
    private VBox playerInfoPane;
    private HBox gameControls;
    private Map<Integer, StackPane> tilePanes;

    public MonopolyGameUI(BoardGame boardGame) {
        super(boardGame);
        this.controller = new MonopolyController(boardGame);
        initializeMonopolyUI();
    }

    private void initializeMonopolyUI() {
        // Add Monopoly-specific UI elements
        setupPropertyInfoPanel();
        setupMonopolyControls();
    }

    private void setupPropertyInfoPanel() {
        propertyInfoPane = new VBox(10);
        propertyInfoPane.setPadding(new Insets(20));
        propertyInfoPane.getStyleClass().add("property-info-panel");
        propertyInfoPane.setVisible(false);

        Label title = new Label("Property Information");
        title.getStyleClass().add("title-label");

        moneyLabel = new Label("Money: $1500");
        moneyLabel.getStyleClass().add("money-label");

        propertiesLabel = new Label("Properties: None");
        propertiesLabel.getStyleClass().add("properties-label");

        propertyInfoPane.getChildren().addAll(title, moneyLabel, propertiesLabel);
        
        // Add the property info panel to the right side of the board
        BorderPane root = (BorderPane) getScene().getRoot();
        HBox rightPanel = new HBox(10);
        rightPanel.getChildren().addAll(getPlayerInfoPane(), propertyInfoPane);
        root.setRight(rightPanel);
    }

    private void setupMonopolyControls() {
        HBox monopolyControls = new HBox(10);
        monopolyControls.setAlignment(javafx.geometry.Pos.CENTER);
        monopolyControls.setPadding(new Insets(10));

        buyPropertyButton = new Button("Buy Property");
        buildHouseButton = new Button("Build House");
        mortgageButton = new Button("Mortgage");

        buyPropertyButton.getStyleClass().add("monopoly-control-button");
        buildHouseButton.getStyleClass().add("monopoly-control-button");
        mortgageButton.getStyleClass().add("monopoly-control-button");

        monopolyControls.getChildren().addAll(buyPropertyButton, buildHouseButton, mortgageButton);

        // Add the controls to the bottom of the board
        BorderPane root = (BorderPane) getScene().getRoot();
        VBox bottomPanel = new VBox(10);
        bottomPanel.getChildren().addAll(getGameControls(), monopolyControls);
        root.setBottom(bottomPanel);
    }

    @Override
    protected void setupBoard() {
        super.setupBoard();
        // Customize the board for Monopoly
        customizeMonopolyBoard();
    }

    private void customizeMonopolyBoard() {
        // Add Monopoly-specific board styling
        for (int i = 0; i < getBoardGame().getBoard().getSizeOfBoard(); i++) {
            Tile tile = getBoardGame().getBoard().getPositionOnBoard(i);
            StackPane tilePane = getTilePanes().get(i);
            
            // Add property-specific styling
            if (tile.getAction() != null) {
                Rectangle tileRect = (Rectangle) tilePane.getChildren().get(0);
                tileRect.getStyleClass().add("monopoly-tile");
                
                // Add property color indicator if applicable
                if (tile.getAction().getDescription().contains("Property")) {
                    Rectangle colorIndicator = new Rectangle(70, 5);
                    colorIndicator.setFill(getPropertyColor(i));
                    tilePane.getChildren().add(colorIndicator);
                }
            }
        }
    }

    private Color getPropertyColor(int position) {
        // Define property colors based on position
        // This is a simplified version - you'll need to implement the actual color logic
        switch (position) {
            case 1: return Color.BROWN;
            case 3: return Color.BROWN;
            case 6: return Color.LIGHTBLUE;
            case 8: return Color.LIGHTBLUE;
            case 9: return Color.LIGHTBLUE;
            // Add more cases for other property groups
            default: return Color.WHITE;
        }
    }

    public void updateMoneyLabel(SimpleMonopolyPlayer player) {
        moneyLabel.setText(String.format("Money: $%d", player.getMoney()));
    }

    public void updatePropertiesLabel(SimpleMonopolyPlayer player) {
        propertiesLabel.setText(String.format("Properties: %d", player.getOwnedProperties().size()));
    }

    public void setBuyPropertyAction(Runnable action) {
        buyPropertyButton.setOnAction(e -> action.run());
    }

    public void setBuildHouseAction(Runnable action) {
        buildHouseButton.setOnAction(e -> action.run());
    }

    public void setMortgageAction(Runnable action) {
        mortgageButton.setOnAction(e -> action.run());
    }

    private void handleBuyProperty() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) getBoardGame().getCurrentPlayer();
        Tile currentTile = currentPlayer.getCurrentTile();
        if (currentTile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) currentTile;
            if (!propertyTile.isOwned()) {
                controller.buyProperty(currentPlayer, propertyTile);
                refreshUIFromBoardGame();
            }
        }
    }

    private void handleBuildHouse() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) getBoardGame().getCurrentPlayer();
        Tile currentTile = currentPlayer.getCurrentTile();
        if (currentTile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) currentTile;
            if (propertyTile.getOwner() == currentPlayer) {
                controller.buildHouse(currentPlayer, propertyTile);
                refreshUIFromBoardGame();
            }
        }
    }

    private void handleMortgage() {
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) getBoardGame().getCurrentPlayer();
        Tile currentTile = currentPlayer.getCurrentTile();
        if (currentTile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) currentTile;
            if (propertyTile.getOwner() == currentPlayer) {
                controller.mortgageProperty(currentPlayer, propertyTile);
                refreshUIFromBoardGame();
            }
        }
    }

    @Override
    public void refreshUIFromBoardGame() {
        super.refreshUIFromBoardGame();
        // Update property info panel
        SimpleMonopolyPlayer currentPlayer = (SimpleMonopolyPlayer) getBoardGame().getCurrentPlayer();
        Tile currentTile = currentPlayer.getCurrentTile();
        
        if (currentTile instanceof PropertyTile) {
            PropertyTile propertyTile = (PropertyTile) currentTile;
            propertyInfoPane.setVisible(true);
            
            // Update button states
            buyPropertyButton.setDisable(propertyTile.isOwned());
            buildHouseButton.setDisable(propertyTile.getOwner() != currentPlayer);
            mortgageButton.setDisable(propertyTile.getOwner() != currentPlayer);
            
            // Update property information
            Label priceLabel = new Label("Price: $" + propertyTile.getPrice());
            Label rentLabel = new Label("Rent: $" + propertyTile.getRent());
            Label ownerLabel = new Label("Owner: " + (propertyTile.getOwner() != null ? propertyTile.getOwner().getName() : "None"));
            
            propertyInfoPane.getChildren().setAll(
                new Label("Property Information"),
                priceLabel,
                rentLabel,
                ownerLabel,
                buyPropertyButton,
                buildHouseButton,
                mortgageButton
            );
        } else {
            propertyInfoPane.setVisible(false);
        }
    }

    public Scene getScene() {
        return super.getScene();
    }
} 