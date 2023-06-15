package com.example.lottikarotti;

import static org.mockito.Mockito.mockStatic;

import com.example.lottikarotti.GameLogic.PlayerMove;
import com.example.lottikarotti.Network.ServerConnection;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

public class MovementTest {

    private MockedStatic<ServerConnection> mockedServerConnection;
    private String playerstill, playermove, playermovefoul, playermovefoul2;

    @BeforeEach
    public void setup() {
        // Mock the ServerConnection class
        mockedServerConnection = mockStatic(ServerConnection.class);
        playerstill = "[{\"sid\":\"LPRTFbJ099djPWfZAAAN\",\"lobbycode\":\"732836\",\"color\":\"white\",\"rabbits\":[{\"name\":\"rabbit1\",\"position\":0},{\"name\":\"rabbit2\",\"position\":0},{\"name\":\"rabbit3\",\"position\":0},{\"name\":\"rabbit4\",\"position\":0}]}]";
        playermove = "[{\"sid\":\"LPRTFbJ099djPWfZAAAN\",\"lobbycode\":\"732836\",\"color\":\"white\",\"rabbits\":[{\"name\":\"rabbit1\",\"position\":3},{\"name\":\"rabbit2\",\"position\":0},{\"name\":\"rabbit3\",\"position\":0},{\"name\":\"rabbit4\",\"position\":0}]}]";
        playermovefoul = "[{\"sid\":\"LPRTFbJ099djPWfZAAAN\",\"lobbycode\":\"732836\",\"color\":\"white\",\"rabbits\":[{\"name\":\"rabbit1\",\"position\":-1},{\"name\":\"rabbit2\",\"position\":0},{\"name\":\"rabbit3\",\"position\":0},{\"name\":\"rabbit4\",\"position\":0}]}]";
        playermovefoul2 = "[{\"sid\":\"LPRTFbJ099djPWfZAAAN\",\"lobbycode\":\"732836\",\"color\":\"white\",\"rabbits\":[{\"name\":\"rabbit1\",\"position\":29},{\"name\":\"rabbit2\",\"position\":0},{\"name\":\"rabbit3\",\"position\":0},{\"name\":\"rabbit4\",\"position\":0}]}]";

    }

    @AfterEach
    public void teardown() {
        // Close the mocked ServerConnection
        mockedServerConnection.close();
    }


    @Test
    public void errorMovementTest() throws JsonProcessingException {

        Assertions.assertThrows(JsonProcessingException.class, () -> {
            PlayerMove.handleMoveFromServer("abc");
        });
    }

    @Test
    public void successMovementTest() {
        Assertions.assertDoesNotThrow(() -> {
            PlayerMove.handleMoveFromServer(playerstill);
        });
    }

    @Test
    public void handleIncomingMoveTest() throws JsonProcessingException {
        List<Player> players = new ArrayList<Player>();
        List<Player> playersServer = new ArrayList<Player>();

        players.add(new Player("LPRTFbJ099djPWfZAAAN", "123456", "white", null));
        playersServer = PlayerMove.handleMoveFromServer(playerstill);

        Assertions.assertEquals(playersServer.get(0).getColor(), players.get(0).getColor());
        Assertions.assertNotEquals(playersServer.get(0).getLobbycode(), players.get(0).getLobbycode());
    }

    @Test
    public void testMovement() throws JsonProcessingException {
        List<Player> players = new ArrayList<Player>();
        List<Player> playersServer = new ArrayList<>();
        players = PlayerMove.handleMoveFromServer(playerstill);

        // Test movement
        Assertions.assertEquals(players.get(0).getRabbits().get(0).getPosition(), 0);

        // Players are moving
        players = PlayerMove.handleMoveFromServer(playermove);
        // Test if moved
        Assertions.assertNotEquals(players.get(0).getRabbits().get(0).getPosition(), 0);
        Assertions.assertEquals(players.get(0).getRabbits().get(0).getPosition(), 3);
    }


    @Test
    public void moveFoulPositionTest() throws JsonProcessingException {
        List<Player> players = new ArrayList<Player>();

        // foul move
        players = PlayerMove.handleMoveFromServer(playermovefoul);

        // Test movement
        Assertions.assertNotEquals(players.get(0).getRabbits().get(0).getPosition(), -1);
        Assertions.assertEquals(players.get(0).getRabbits().get(0).getPosition(), 0);
    }

    @Test
    public void moveFoulPositionTest2() throws JsonProcessingException{
        List<Player> players = new ArrayList<Player>();

        // foul move
        players = PlayerMove.handleMoveFromServer(playermovefoul2);

        // Test movement
        Assertions.assertNotEquals(players.get(0).getRabbits().get(0).getPosition(), 29);
        Assertions.assertEquals(players.get(0).getRabbits().get(0).getPosition(), 0);
    }
}

