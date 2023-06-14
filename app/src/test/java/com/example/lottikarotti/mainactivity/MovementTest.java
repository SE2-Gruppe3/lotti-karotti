package com.example.lottikarotti.mainactivity;

import static org.mockito.Mockito.*;
import com.example.lottikarotti.GameLogic.PlayerMove;
import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.Player;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.json.JsonMapper;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MovementTest {

    private MockedStatic<ServerConnection> mockedServerConnection;

    @BeforeEach
    public void setup() {
    // Mock the ServerConnection class
        mockedServerConnection = mockStatic(ServerConnection.class);
    }

    @AfterEach
    public void teardown() {
        // Close the mocked ServerConnection
        mockedServerConnection.close();
    }


    @Test
    public void testIncomingMovement() throws JsonProcessingException {
        String json = "[{\"sid\":\"LPRTFbJ099djPWfZAAAN\",\"lobbycode\":\"732836\",\"color\":\"white\",\"rabbits\":[{\"name\":\"rabbit1\",\"position\":0},{\"name\":\"rabbit2\",\"position\":0},{\"name\":\"rabbit3\",\"position\":0},{\"name\":\"rabbit4\",\"position\":0}]}]";

        Assertions.assertThrows(JsonProcessingException.class, () -> {
            PlayerMove.handleMoveFromServer("abc");
        });

        Assertions.assertDoesNotThrow(() -> {
            PlayerMove.handleMoveFromServer(json);
        });

        List<Player> players = new ArrayList<Player>();
        players.add(new Player("LPRTFbJ099djPWfZAAAN", "732836", "white", null));

        Assertions.assertNotEquals(PlayerMove.handleMoveFromServer(json).toArray().toString(), players.toArray().toString());
    }
}

