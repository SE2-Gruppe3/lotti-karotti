package com.example.lottikarotti.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
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
import butterknife.OnFocusChange;
import butterknife.OnTextChanged;
import butterknife.OnTouch;
import io.socket.client.Socket;

public class LobbyActivity extends AppCompatActivity {
    @BindView(R.id.etLobbyId)
    EditText etlobbyId;

    Button startBtn;
    Button joinBtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
        EditText etlobbyId = (EditText) findViewById(R.id.etLobbyId);
       startBtn = (Button) findViewById(R.id.btnStartGame);
       startBtn.setEnabled(false);

        joinBtn = (Button) findViewById(R.id.btnJoinGame);
        joinBtn.setEnabled(false);




        ButterKnife.bind(this);
    }


     @OnTextChanged(R.id.usernameTextView)
     void checkStart(){
         EditText etUserName = (EditText) findViewById(R.id.usernameTextView);

         String strUserName = etUserName.getText().toString();

         if(TextUtils.isEmpty(strUserName)) {
             etUserName.setError("username must be set");
             startBtn.setEnabled(false);

         }else{
             startBtn.setEnabled(true);
         }
     }
    @OnTextChanged(R.id.etLobbyId)
    void checkJoin(){
        EditText etlobbyId = (EditText) findViewById(R.id.etLobbyId);

        String id = etlobbyId.getText().toString();

        if(TextUtils.isEmpty(id)) {
            etlobbyId.setError("lobby id must be set to join");
            joinBtn.setEnabled(false);

        }else{
            joinBtn.setEnabled(true);
        }
    }
    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {

        Random rand = new Random();
        int lobbycode = rand.nextInt(800000);
         startGameActivity(lobbycode, etlobbyId.getText().toString(),"start");
    }
    private void startGameActivity(Integer lobbyId, String username, String info) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("lobbyId", String.valueOf(lobbyId));
        intent.putExtra("username", String.valueOf(username));
        intent.putExtra("info", String.valueOf(info));
        startActivity(intent);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {

        try{final int id = Integer.parseInt(etlobbyId.getText().toString());

            startGameActivity(id,etlobbyId.getText().toString(),"join");

        }
        catch (Exception ex){
            Toast.makeText(getBaseContext(), "Please type valid number", Toast.LENGTH_SHORT).show();
        }



    }


}