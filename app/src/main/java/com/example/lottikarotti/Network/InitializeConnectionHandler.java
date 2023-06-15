package com.example.lottikarotti.Network;

import android.util.Log;
import android.widget.Toast;

import com.example.lottikarotti.MainActivity;
import com.example.lottikarotti.Player;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;

public class InitializeConnectionHandler {

    public static Socket initialize(Socket socket, String URI, MainActivity context){
        try {
            socket = ServerConnection.getInstance(URI);
            ServerConnection.connect();
            Log.d("InitializeConnectionHandler", "onCreate: Connected to server");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        /// Example of getting server response using callbacks - We get here online player count back
        ServerConnection.getNumberOfConnectedPlayers(context, new ServerConnection.PlayerCountCallback() {
            @Override
            public int onPlayerCountReceived(int count) {
                Toast.makeText(context, "Online players: " + count, Toast.LENGTH_SHORT).show();
                return count;
            }
        });
        return socket;
    }
}
