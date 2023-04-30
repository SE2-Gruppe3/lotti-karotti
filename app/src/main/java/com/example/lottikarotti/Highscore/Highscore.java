package com.example.lottikarotti.Highscore;

public class Highscore implements Comparable<Highscore> {
    public String username;
    public int score;

    public Highscore(String username, int score) {
        this.username = username;
        this.score = score;
    }

    @Override
    public int compareTo(Highscore other) {
        return Integer.compare(score, other.score);
    }
}
