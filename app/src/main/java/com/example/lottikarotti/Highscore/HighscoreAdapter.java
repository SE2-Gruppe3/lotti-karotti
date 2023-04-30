package com.example.lottikarotti.Highscore;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.lottikarotti.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HighscoreAdapter extends RecyclerView.Adapter<HighscoreViewHolder> {
    private List<String> usernames;
    private List<Integer> scores;

    public void setData(List<String> usernames, List<Integer> scores) {
        List<Highscore> highscores = new ArrayList();
        for(int i = 0; i < usernames.size(); i++) {
            highscores.add(new Highscore(usernames.get(i), scores.get(i)));
        }
        Collections.sort(highscores, Collections.reverseOrder());

        this.usernames = new ArrayList<>();
        this.scores = new ArrayList<>();
        for(Highscore highscore : highscores) {
            this.usernames.add(highscore.username);
            this.scores.add(highscore.score);
        }

        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public HighscoreViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.highscore_item, parent, false);

        return new HighscoreViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HighscoreViewHolder holder, int position) {
        holder.bind(usernames.get(position), scores.get(position));
    }

    @Override
    public int getItemCount() {
        if (usernames == null) {
            return 0;
        }
        return usernames.size();
    }
}