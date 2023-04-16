package com.example.lottikarotti;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;

import com.example.lottikarotti.Network.ServerConnection;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ServerConnection.setSocket();
        ServerConnection.establishConnection();

        Socket socket = ServerConnection.getSocket();

        socket.on("getplayers", args -> {
            int number = (Integer) args[0];
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), "Number of connected players: " + number, Toast.LENGTH_SHORT).show();
                }
            });
        });

        socket.emit("getplayers");
    }
}