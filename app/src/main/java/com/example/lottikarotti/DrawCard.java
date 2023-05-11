package com.example.lottikarotti;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lottikarotti.Network.ServerConnection;

public class DrawCard {
    final static int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };

    public static void draw(ServerConnection serverConnection, View drawButton, TextView instructions, Button carrotButton, ImageView cardView, User user) {
        serverConnection.drawCard(new ServerConnection.DrawCardCallback() {
            @Override
            public void onCardDrawn(int random) {
                cardView.setImageResource(cards[random]);

                switch (random) {
                    case 0:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move three fields with your rabbit on the game board!");
                        //Move logic comes here
                        break;
                    case 1:
                        carrotButton.setEnabled(true);
                        drawButton.setEnabled(false);
                        instructions.setText("Click the carrot on the game board!");
                        break;
                    case 2:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move one field with your rabbit on the game board!");
                        //Move logic comes here
                        break;
                    case 3:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move two fields with your rabbit on the game board!");
                        //Move logic comes here
                        break;
                }
            }
        });
    }
}
