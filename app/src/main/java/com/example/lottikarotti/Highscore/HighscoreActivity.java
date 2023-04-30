package com.example.lottikarotti.Highscore;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.example.lottikarotti.Network.ServerConnection;
import com.example.lottikarotti.R;

import java.net.URISyntaxException;
import java.util.List;

public class HighscoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HighscoreAdapter adapter;
    private ServerConnection serverConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HighscoreAdapter();

        try {
            serverConnection = new ServerConnection("http://10.2.0.141:3000/", this);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        serverConnection.connect();

        serverConnection.getHighScoreBoard(new ServerConnection.HighScoreBoardCallback() {
            @Override
            public void onHighScoreBoardReceived(List<String> usernames, List<Integer> scores) {
                adapter.setData(usernames, scores);
                Toast.makeText(getApplicationContext(), "Username:" + usernames.get(0) + " Score: " + scores.get(0), Toast.LENGTH_SHORT).show();
            }
        });

        recyclerView.setAdapter(adapter);
    }
}