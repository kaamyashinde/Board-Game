package edu.ntnu.iir.bidata.model.player;

import edu.ntnu.iir.bidata.model.tile.core.monopoly.PropertyTile;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;

public class SimpleMonopolyPlayer extends Player{
    @Getter
    private int money;
    @Getter
    private List<PropertyTile> ownedProperties;

    public SimpleMonopolyPlayer(String name) {
        super(name);
        this.money = 1500;
        this.ownedProperties = new ArrayList<>();
    }
    
}
