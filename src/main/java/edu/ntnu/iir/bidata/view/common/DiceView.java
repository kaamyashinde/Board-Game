package edu.ntnu.iir.bidata.view.common;

import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Circle;
import javafx.scene.Group;

public class DiceView extends HBox {
    private static final int SIZE = 50;
    private static final int DOT_SIZE = 6;
    private static final int PADDING = 10;
    private final SingleDieView die1;
    private final SingleDieView die2;

    public DiceView() {
        setSpacing(16);
        die1 = new SingleDieView();
        die2 = new SingleDieView();
        getChildren().addAll(die1, die2);
        setValues(1, 1);
    }

    public void setValues(int value1, int value2) {
        die1.setValue(value1);
        die2.setValue(value2);
    }

    // For backward compatibility: sets both dice to the same value
    public void setValue(int value) {
        setValues(value, value);
    }

    // Inner class for a single die
    private static class SingleDieView extends StackPane {
        private final Rectangle diceFace;
        private final Group dots;

        public SingleDieView() {
            diceFace = new Rectangle(SIZE, SIZE);
            diceFace.setFill(Color.WHITE);
            diceFace.setStroke(Color.BLACK);
            diceFace.setStrokeWidth(2);
            diceFace.setArcWidth(10);
            diceFace.setArcHeight(10);
            dots = new Group();
            getChildren().addAll(diceFace, dots);
            setValue(1);
        }

        public void setValue(int value) {
            dots.getChildren().clear();
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
} 