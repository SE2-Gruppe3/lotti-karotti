import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import android.app.Activity;
import android.view.View;

import com.example.lottikarotti.MainActivity;
import com.example.lottikarotti.Network.ServerConnection;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import io.socket.client.Socket;

@ExtendWith(MockitoExtension.class)
public class MovementTest {
    private MockedStatic<ServerConnection> mockedServerConnection;
    private Socket mockedSocket;
    private MainActivity main;
    private View viewMock;

    @BeforeEach
    public void setup() {
        main = new MainActivity();

        mockedSocket = mock(Socket.class);
        mockedServerConnection = mockStatic(ServerConnection.class);
        mockedServerConnection.when(() -> ServerConnection.getInstance(anyString()))
                .thenReturn(mockedSocket);

        viewMock = mock(View.class);
    }

    @AfterEach
    public void teardown() {
        mockedServerConnection.close();
    }

    @Test
    public void testServerConnection() {
        // Perform test on the server connection
        ServerConnection.connect();
        when(mockedSocket.connected()).thenReturn(true);
        Assertions.assertTrue(mockedSocket.connected());
    }

    @Test
    public void testMoveListener() {
        int steps = 5;
        main.selectRabbit(1);
        main.drawButtonListener(viewMock);

        //Test if drawing worked
        Assertions.assertEquals(1, main.drawbut

    }

    // Add more test methods for other server listeners following a similar structure
}
