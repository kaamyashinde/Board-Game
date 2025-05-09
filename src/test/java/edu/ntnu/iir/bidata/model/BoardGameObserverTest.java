package edu.ntnu.iir.bidata.model;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.*;

class BoardGameObserverTest {
    @Mock
    private Player mockPlayer;
    
    @Test
    void testObserverInterface() {
        BoardGameObserver observer = mock(BoardGameObserver.class);
        
        // Test onPlayerMoved
        observer.onPlayerMoved(mockPlayer, 5);
        verify(observer).onPlayerMoved(mockPlayer, 5);
        
        // Test onGameWon
        observer.onGameWon(mockPlayer);
        verify(observer).onGameWon(mockPlayer);
        
        // Test onTurnChanged
        observer.onTurnChanged(mockPlayer);
        verify(observer).onTurnChanged(mockPlayer);
    }
    
    @Test
    void testMultipleObserverNotifications() {
        BoardGameObserver observer1 = mock(BoardGameObserver.class);
        BoardGameObserver observer2 = mock(BoardGameObserver.class);
        
        observer1.onPlayerMoved(mockPlayer, 3);
        observer2.onPlayerMoved(mockPlayer, 3);
        
        verify(observer1).onPlayerMoved(mockPlayer, 3);
        verify(observer2).onPlayerMoved(mockPlayer, 3);
    }
} 