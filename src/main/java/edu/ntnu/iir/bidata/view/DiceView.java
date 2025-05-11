package edu.ntnu.iir.bidata.view;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.Group;

public class DiceView extends StackPane {
    private static final int SIZE = 50;
    private static final int DOT_SIZE = 6;
    private static final int PADDING = 10;
    
    private final Rectangle diceFace;
    private final Group dots;
    
    public DiceView() {
        // Create the dice face
        diceFace = new Rectangle(SIZE, SIZE);
        diceFace.setFill(Color.WHITE);
        diceFace.setStroke(Color.BLACK);
        diceFace.setStrokeWidth(2);
        diceFace.setArcWidth(10);
        diceFace.setArcHeight(10);
        
        // Create the dots container
        dots = new Group();
        
        // Add all components
        getChildren().addAll(diceFace, dots);
        
        // Set initial value
        setValue(1);
    }
    
    public void setValue(int value) {
        // Clear existing dots
        dots.getChildren().clear();
        
        // Create dots based on value
        switch (value) {
            case 1:
                createDot(SIZE/2, SIZE/2);
                break;
            case 2:
                createDot(PADDING, PADDING);
                createDot(SIZE-PADDING, SIZE-PADDING);
                break;
            case 3:
                createDot(PADDING, PADDING);
                createDot(SIZE/2, SIZE/2);
                createDot(SIZE-PADDING, SIZE-PADDING);
                break;
            case 4:
                createDot(PADDING, PADDING);
                createDot(SIZE-PADDING, PADDING);
                createDot(PADDING, SIZE-PADDING);
                createDot(SIZE-PADDING, SIZE-PADDING);
                break;
            case 5:
                createDot(PADDING, PADDING);
                createDot(SIZE-PADDING, PADDING);
                createDot(SIZE/2, SIZE/2);
                createDot(PADDING, SIZE-PADDING);
                createDot(SIZE-PADDING, SIZE-PADDING);
                break;
            case 6:
                createDot(PADDING, PADDING);
                createDot(SIZE-PADDING, PADDING);
                createDot(PADDING, SIZE/2);
                createDot(SIZE-PADDING, SIZE/2);
                createDot(PADDING, SIZE-PADDING);
                createDot(SIZE-PADDING, SIZE-PADDING);
                break;
        }
    }
    
    private void createDot(double x, double y) {
        Circle dot = new Circle(x, y, DOT_SIZE);
        dot.setFill(Color.BLACK);
        dots.getChildren().add(dot);
    }
} 