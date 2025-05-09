package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.view.GameUI;

/**
 * Factory class that provides pre-defined board game configurations.
 * This class encapsulates different game setups that can be used to create board games.
 * 
 * @author kaamyashinde 
 * @version 1.0.0
 */
public class BoardGameFactory {
    
    /**
     * Creates a small board game configuration suitable for quick games.
     * 
     * @param ui The UI implementation to use
     * @return A configured BoardGame instance
     */
    public static BoardGame createSmallGame(GameUI ui) {
        return new BoardGame(1, 2, 10, ui);
    }

    /**
     * Creates a medium board game configuration suitable for standard games.
     * 
     * @param ui The UI implementation to use
     * @return A configured BoardGame instance
     */
    public static BoardGame createMediumGame(GameUI ui) {
        return new BoardGame(2, 3, 20, ui);
    }

    /**
     * Creates a large board game configuration suitable for longer games.
     * 
     * @param ui The UI implementation to use
     * @return A configured BoardGame instance
     */
    public static BoardGame createLargeGame(GameUI ui) {
        return new BoardGame(2, 4, 30, ui);
    }
} 