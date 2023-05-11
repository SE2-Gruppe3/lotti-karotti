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

import io.socket.client.Socket;

public class HighscoreActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private HighscoreAdapter adapter;
    private ServerConnection serverConnection;
    Socket socket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_highscore);

        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HighscoreAdapter();

        try {
            socket = ServerConnection.getInstance("xxx");
        } catch (Exception ex) {
        }


        serverConnection.connect();

        serverConnection.getHighScoreBoard(HighscoreActivity.this, new ServerConnection.HighScoreBoardCallback() {
            @Override
            public void onHighScoreBoardReceived(List<String> usernames, List<Integer> scores) {
                adapter.setData(usernames, scores);
            }
        });


        serverConnection.updateHighScoreBoard("Amar");

        recyclerView.setAdapter(adapter);
    }
}