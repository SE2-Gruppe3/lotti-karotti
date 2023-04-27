package com.example.lottikarotti.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lottikarotti.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LobbyActivity extends AppCompatActivity {
    @BindView(R.id.etLobbyId)
    TextView lobbyId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);


        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {
        startGameActivity(null);
    }
    private void startGameActivity(Integer lobbyId) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("lobbyID", lobbyId);
        startActivity(intent);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {

        try{final int lobby = Integer.parseInt(lobbyId.getText().toString());
            startGameActivity(lobby);}
        catch (Exception ex){
            Toast.makeText(getBaseContext(), "Please type valid number", Toast.LENGTH_SHORT).show();
        }



    }
}