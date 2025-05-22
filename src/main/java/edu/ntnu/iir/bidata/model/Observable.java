package edu.ntnu.iir.bidata.model;

/**
 * The Observable interface represents the subject in the Observer design pattern. It allows
 * multiple observers to register, be notified of state changes, and be unregistered as needed.
 * Classes implementing this interface serve as the observable entity whose state changes are of
 * interest to observers.
 */
public interface Observable {

  /**
   * Registers an observer to the observable entity to receive updates when the observable state
   * changes.
   *
   * @param observer the Observer to be registered, enabling it to receive notifications of state
   *     changes in the observable
   */
  void addObserver(Observer observer);

  /**
   * Unregisters an observer from the observable entity so that it no longer receives updates when
   * the observable state changes.
   *
   * @param observer the Observer to be removed from the list of registered observers
   */
  void removeObserver(Observer observer);

  /**
   * Notifies all registered observers of a change in the observable state. This method is typically
   * called to update observers about modifications to the observable object, triggering their
   * respective update functionality.
   */
  void notifyObservers();
}
