package com.example.lottikarotti.Highscore;

import com.example.lottikarotti.Network.ServerConnection;

public class HighscoreManager {
    private ServerConnection serverConnection;

    public HighscoreManager(ServerConnection serverConnection){
        this.serverConnection = serverConnection;
        serverConnection.connect();
    }

    public interface ScoreboardUpdateCallback {
        void onScoreboardUpdate(String username, int score);
    }
}
