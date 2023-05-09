package com.example.lottikarotti.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lottikarotti.Models.User;
import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.R;

import java.net.URISyntaxException;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.socket.client.Socket;

public class LobbyActivity extends AppCompatActivity {
    @BindView(R.id.etLobbyId)
    TextView lobbId;
    Button startBtn;
    Button joinBtn;
    Socket socket ;
    String serverUrl = "http://143.205.186.74:3000";
    ServerConnection serverConnection;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);

       startBtn = (Button) findViewById(R.id.btnStartGame);
       startBtn.setEnabled(false);

        joinBtn = (Button) findViewById(R.id.btnJoinGame);
        joinBtn.setEnabled(false);

        EditText etUserName = (EditText) findViewById(R.id.usernameTextView);
        String strUserName = etUserName.getText().toString();

        if(TextUtils.isEmpty(strUserName)) {
            etUserName.setError("Username must be set");
            return;
        }
       setUpNetwork(socket);

        ButterKnife.bind(this);
    }


    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {

        Random rand = new Random();

        int lobbycode = rand.nextInt(800000);
        serverConnection.registerNewPlayer("Amar");
        serverConnection.createNewLobby(String.valueOf(lobbycode));
        startGameActivity(lobbycode);
    }
    private void startGameActivity(Integer lobbyId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("lobbyId", String.valueOf(lobbyId));
        startActivity(intent);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {

        try{final int id = Integer.parseInt(lobbId.getText().toString());

            serverConnection.registerNewPlayer("Amar");
            serverConnection.joinLobby("123456");
            startGameActivity(id);

        }
        catch (Exception ex){
            Toast.makeText(getBaseContext(), "Please type valid number", Toast.LENGTH_SHORT).show();
        }



    }

    private void setUpNetwork(Socket socket){

        try {
            serverConnection = new ServerConnection(serverUrl);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        serverConnection.connect();
        socket = serverConnection.getSocket();



    }
}