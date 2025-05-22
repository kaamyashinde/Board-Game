package edu.ntnu.iir.bidata.model;

/**
 * The Observer interface defines the contract for objects that are notified about changes
 * in the state of an observable subject.
 *
 * Implementing classes should define the specific behavior to be executed when an update
 * notification is received.
 */
public interface Observer {

  /**
   * This method is called to notify the implementing class about a change in the state of
   * the observable subject. The specific behavior to be executed upon receiving the update
   * should be defined in the implementing class.
   */
  void update();
}
