package edu.ntnu.iir.bidata.model;

import edu.ntnu.iir.bidata.model.Observer;

public interface Observable {
    void addObserver(Observer observer);
    void removeObserver(Observer observer);
    void notifyObservers();
} 