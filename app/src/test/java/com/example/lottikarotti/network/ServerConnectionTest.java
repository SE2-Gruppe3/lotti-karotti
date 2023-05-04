package com.example.lottikarotti.network;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.app.Activity;

import com.example.lottikarotti.Network.ServerConnection;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.net.URISyntaxException;

import io.socket.client.Socket;

@ExtendWith(MockitoExtension.class)
public class ServerConnectionTest {

    @Mock
    private Socket socket;

    @Mock
    private Activity activity;

    private ServerConnection serverConnection;

    @BeforeEach
    public void setup() throws URISyntaxException {
        serverConnection = new ServerConnection(socket, activity);
    }

    @Test
    public void testConnect() {
        when(socket.connected()).thenReturn(false);
        serverConnection.connect();

        verify(socket, times(1)).connect();
    }

    @Test
    public void testDisconnect() {
        when(socket.connected()).thenReturn(true);
        serverConnection.disconnect();

        verify(socket, times(1)).disconnect();
    }

    @Test
    public void testCheckIfConnectionIsAlive() {
        ServerConnection.ConnectionCallback callback = Mockito.mock(ServerConnection.ConnectionCallback.class);
        serverConnection.checkIfConnectionIsAlive(callback);

        verify(socket, times(1)).on(eq("alive"), any());
        verify(socket, times(1)).emit("alive");
    }

    @Test
    public void testGetNumberOfConnectedPlayers() {
        ServerConnection.PlayerCountCallback callback = Mockito.mock(ServerConnection.PlayerCountCallback.class);
        serverConnection.getNumberOfConnectedPlayers(callback);

        verify(socket, times(1)).on(eq("getplayers"), any());
        verify(socket, times(1)).emit("getplayers");
    }

    @Test
    public void testGetListOfConnectedPlayers() {
        ServerConnection.PlayerListCallback callback = Mockito.mock(ServerConnection.PlayerListCallback.class);
        serverConnection.getListOfConnectedPlayers(callback);

        verify(socket, times(1)).on(eq("getplayerlist"), any());
        verify(socket, times(1)).emit("getplayerlist");
    }

    @Test
    public void testRegisterNewPlayer() {
        String username = "player1";
        serverConnection.registerNewPlayer(username);

        verify(socket, times(1)).emit("register", username);
    }

    @Test
    public void testCreateNewLobby() {
        String lobbyCode = "123456";
        serverConnection.createNewLobby(lobbyCode);

        verify(socket, times(1)).emit("createlobby", lobbyCode);
    }

    @Test
    public void testJoinLobby() {
        String lobbyCode = "123456";
        serverConnection.joinLobby(lobbyCode);

        verify(socket, times(1)).emit("joinlobby", lobbyCode);
    }
}
