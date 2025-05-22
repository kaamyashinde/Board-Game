package edu.ntnu.iir.bidata.model.utils;

import edu.ntnu.iir.bidata.model.utils.DefaultGameMediator.GameMediatorListener;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DefaultGameMediatorTest {

  @Mock
  private GameMediatorListener mockListener1;

  @Mock
  private GameMediatorListener mockListener2;

  @Mock
  private GameMediatorListener mockListener3;

  @Mock
  private Object mockSender;

  private DefaultGameMediator mediator;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
    mediator = new DefaultGameMediator();
  }

  @Test
  void testConstructor() {
    // Arrange & Act
    DefaultGameMediator newMediator = new DefaultGameMediator();

    // Assert
    assertNotNull(newMediator);
  }

  @Test
  void testRegisterSingleListener() {
    // Arrange & Act
    mediator.register(mockListener1);

    // Assert
    // Verify by notifying and checking if listener receives the event
    mediator.notify(mockSender, "testEvent");
    verify(mockListener1).onEvent(mockSender, "testEvent");
  }

  @Test
  void testRegisterMultipleListeners() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener2);
    mediator.register(mockListener3);

    // Act
    mediator.notify(mockSender, "multipleListenersEvent");

    // Assert
    verify(mockListener1).onEvent(mockSender, "multipleListenersEvent");
    verify(mockListener2).onEvent(mockSender, "multipleListenersEvent");
    verify(mockListener3).onEvent(mockSender, "multipleListenersEvent");
  }

  @Test
  void testRegisterSameListenerTwice() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener1);

    // Act
    mediator.notify(mockSender, "duplicateEvent");

    // Assert
    // Should only be called once since Set doesn't allow duplicates
    verify(mockListener1, times(1)).onEvent(mockSender, "duplicateEvent");
  }

  @Test
  void testUnregisterListener() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener2);

    // Act
    mediator.unregister(mockListener1);
    mediator.notify(mockSender, "afterUnregisterEvent");

    // Assert
    verify(mockListener1, never()).onEvent(mockSender, "afterUnregisterEvent");
    verify(mockListener2).onEvent(mockSender, "afterUnregisterEvent");
  }

  @Test
  void testUnregisterNonExistentListener() {
    // Arrange
    mediator.register(mockListener1);

    // Act
    mediator.unregister(mockListener2); // Not registered
    mediator.notify(mockSender, "nonExistentUnregisterEvent");

    // Assert
    verify(mockListener1).onEvent(mockSender, "nonExistentUnregisterEvent");
    verify(mockListener2, never()).onEvent(mockSender, "nonExistentUnregisterEvent");
  }

  @Test
  void testUnregisterAllListeners() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener2);
    mediator.register(mockListener3);

    // Act
    mediator.unregister(mockListener1);
    mediator.unregister(mockListener2);
    mediator.unregister(mockListener3);
    mediator.notify(mockSender, "noListenersEvent");

    // Assert
    verify(mockListener1, never()).onEvent(mockSender, "noListenersEvent");
    verify(mockListener2, never()).onEvent(mockSender, "noListenersEvent");
    verify(mockListener3, never()).onEvent(mockSender, "noListenersEvent");
  }

  @Test
  void testNotifyWithNullSender() {
    // Arrange
    mediator.register(mockListener1);

    // Act
    mediator.notify(null, "nullSenderEvent");

    // Assert
    verify(mockListener1).onEvent(null, "nullSenderEvent");
  }

  @Test
  void testNotifyWithNullEvent() {
    // Arrange
    mediator.register(mockListener1);

    // Act
    mediator.notify(mockSender, null);

    // Assert
    verify(mockListener1).onEvent(mockSender, null);
  }

  @Test
  void testNotifyWithEmptyEvent() {
    // Arrange
    mediator.register(mockListener1);

    // Act
    mediator.notify(mockSender, "");

    // Assert
    verify(mockListener1).onEvent(mockSender, "");
  }

  @Test
  void testNotifyWithNoListeners() {
    // Arrange
    // No listeners registered

    // Act & Assert
    assertDoesNotThrow(() -> mediator.notify(mockSender, "noListenersEvent"));
  }

  @Test
  void testNotifyMultipleEvents() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener2);

    // Act
    mediator.notify(mockSender, "firstEvent");
    mediator.notify(mockSender, "secondEvent");

    // Assert
    verify(mockListener1).onEvent(mockSender, "firstEvent");
    verify(mockListener1).onEvent(mockSender, "secondEvent");
    verify(mockListener2).onEvent(mockSender, "firstEvent");
    verify(mockListener2).onEvent(mockSender, "secondEvent");
  }

  @Test
  void testConcurrentModification() {
    // Arrange
    mediator.register(mockListener1);

    // Create a listener that modifies the collection during iteration
    GameMediatorListener modifyingListener = new GameMediatorListener() {
      @Override
      public void onEvent(Object sender, String event) {
        // This would normally cause ConcurrentModificationException
        mediator.register(mockListener2);
        mediator.unregister(mockListener1);
      }
    };

    mediator.register(modifyingListener);

    // Act & Assert
    assertDoesNotThrow(() -> mediator.notify(mockSender, "concurrentModificationTest"));
  }

  @Test
  void testMultipleSenders() {
    // Arrange
    Object sender1 = new Object();
    Object sender2 = new Object();
    mediator.register(mockListener1);

    // Act
    mediator.notify(sender1, "event1");
    mediator.notify(sender2, "event2");

    // Assert
    verify(mockListener1).onEvent(sender1, "event1");
    verify(mockListener1).onEvent(sender2, "event2");
  }

  @Test
  void testListenerExceptionHandling() {
    // Arrange
    GameMediatorListener throwingListener = mock(GameMediatorListener.class);
    doThrow(new RuntimeException("Test exception")).when(throwingListener).onEvent(any(), any());

    mediator.register(throwingListener);
    mediator.register(mockListener1);

    // Act & Assert
    // The forEach should continue even if one listener throws an exception
    // This depends on the forEach implementation, but typically it would propagate
    assertThrows(RuntimeException.class, () -> mediator.notify(mockSender, "exceptionEvent"));
  }

  @Test
  void testRegisterAfterNotify() {
    // Arrange
    mediator.register(mockListener1);
    mediator.notify(mockSender, "beforeRegister");

    // Act
    mediator.register(mockListener2);
    mediator.notify(mockSender, "afterRegister");

    // Assert
    verify(mockListener1).onEvent(mockSender, "beforeRegister");
    verify(mockListener1).onEvent(mockSender, "afterRegister");
    verify(mockListener2, never()).onEvent(mockSender, "beforeRegister");
    verify(mockListener2).onEvent(mockSender, "afterRegister");
  }

  @Test
  void testUnregisterAfterNotify() {
    // Arrange
    mediator.register(mockListener1);
    mediator.register(mockListener2);
    mediator.notify(mockSender, "beforeUnregister");

    // Act
    mediator.unregister(mockListener1);
    mediator.notify(mockSender, "afterUnregister");

    // Assert
    verify(mockListener1).onEvent(mockSender, "beforeUnregister");
    verify(mockListener1, never()).onEvent(mockSender, "afterUnregister");
    verify(mockListener2).onEvent(mockSender, "beforeUnregister");
    verify(mockListener2).onEvent(mockSender, "afterUnregister");
  }

  @Test
  void testEventTypes() {
    // Arrange
    mediator.register(mockListener1);

    // Act
    mediator.notify(mockSender, "nextPlayer");
    mediator.notify(mockSender, "gameOver");
    mediator.notify(mockSender, "propertyPurchased");

    // Assert
    verify(mockListener1).onEvent(mockSender, "nextPlayer");
    verify(mockListener1).onEvent(mockSender, "gameOver");
    verify(mockListener1).onEvent(mockSender, "propertyPurchased");
  }
}