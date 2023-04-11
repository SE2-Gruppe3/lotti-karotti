package com.example.lottikarotti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private Button carrotButton;
    private Button drawButton;
    private ImageView cardView;
    private ImageView hole;
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = {
            R.id.hole1, R.id.hole2, R.id.hole7 };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carrotButton= (Button) findViewById(R.id.carrotButton);
        cardView = (ImageView) findViewById(R.id.imageViewCard);
        drawButton = (Button) findViewById(R.id.drawCard);
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        carrotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int idx = ThreadLocalRandom.current().nextInt(0, 3);
                hole = (ImageView) findViewById(holes[idx]);
                hole.setImageResource(R.drawable.hole);
                boolean carrotClicked = false;
                carrotButton.setEnabled(carrotClicked);

            }
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int random = ThreadLocalRandom.current().nextInt(0, 3);
                cardView.setImageResource(cards[random]);
                if(random ==1){
                    boolean pressCarrot = true;
                    carrotButton.setEnabled(pressCarrot);
                }

            }
        });
    }
}