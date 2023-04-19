package com.example.lottikarotti;

import static com.example.lottikarotti.Network.ServerConnection.checkIfConnectionIsAlive;
import static com.example.lottikarotti.Network.ServerConnection.createNewLobby;
import static com.example.lottikarotti.Network.ServerConnection.getListOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getNumberOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getSocket;
import static com.example.lottikarotti.Network.ServerConnection.registerNewPlayer;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Socket socket = getSocket();

        checkIfConnectionIsAlive(socket, this);
        getNumberOfConnectedPlayers(socket, this);
        registerNewPlayer(socket, "Robot");
        createNewLobby(socket, 1234567);
        getListOfConnectedPlayers(socket, this);

    }
}