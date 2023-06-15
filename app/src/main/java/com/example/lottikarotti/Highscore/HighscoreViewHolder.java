package com.example.lottikarotti.Highscore;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lottikarotti.R;

public class HighscoreViewHolder extends  RecyclerView.ViewHolder {
    private TextView usernameTextView;
    private TextView scoreTextView;

    public HighscoreViewHolder(@NonNull View itemView) {
        super(itemView);
        usernameTextView = itemView.findViewById(R.id.usernameTextView);
        scoreTextView = itemView.findViewById(R.id.scoreTextView);
    }

    public void bind(String username, int score) {
        if (usernameTextView != null) {
            usernameTextView.setText(username);
        }
        if (scoreTextView != null) {
            scoreTextView.setText(String.valueOf(score));
        }
    }
}