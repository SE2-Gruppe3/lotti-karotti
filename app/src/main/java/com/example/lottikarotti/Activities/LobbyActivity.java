package com.example.lottikarotti.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.lottikarotti.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class LobbyActivity extends AppCompatActivity {
    @BindView(R.id.etIP)
    TextView ip;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lobby);
    }

    @OnClick(R.id.btnStartGame)
    void onBtnStartGameClick() {
        startGameActivity(null);
    }
    private void startGameActivity(String host) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("host", host);
        startActivity(intent);
    }

    @OnClick(R.id.btnJoinGame)
    void onBtnJoinGameClick() {
        final String host = ip.getText().toString();

        if(!host.isEmpty()){
            startGameActivity(host);
        }
    }
}