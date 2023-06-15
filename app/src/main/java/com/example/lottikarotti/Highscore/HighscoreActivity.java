package com.example.lottikarotti.Highscore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.lottikarotti.MenuActivity;
import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.R;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.Socket;

public class HighscoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HighscoreAdapter adapter;
    Socket socket;
    private Button exitHighscore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HighscoreAdapter();

        try {
            socket = ServerConnection.getInstance("http://10.2.0.60:3000");
            ServerConnection.connect();
        } catch (Exception ex) {
        }


        ServerConnection.getHighScoreBoard(HighscoreActivity.this, new ServerConnection.HighScoreBoardCallback() {
            @Override
            public void onHighScoreBoardReceived(List<String> usernames, List<Integer> scores) {
                adapter.setData(usernames, scores);
            }
        });

        recyclerView.setAdapter(adapter);

        exitHighscore = findViewById(R.id.button_exitHighscore);
        exitHighscore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(HighscoreActivity.this, MenuActivity.class);
                startActivity(intent);
                finish(); // Optional: Finish the current activity if you don't want to return to it

            }
        });
    }
}