package edu.ntnu.iir.bidata.view.common;

import java.util.List;
import java.util.Map;

public class PlayerSelectionResult {
    public final List<String> playerNames;
    public final Map<String, String> playerTokens;
    public PlayerSelectionResult(List<String> playerNames, Map<String, String> playerTokens) {
        this.playerNames = playerNames;
        this.playerTokens = playerTokens;
    }
} 