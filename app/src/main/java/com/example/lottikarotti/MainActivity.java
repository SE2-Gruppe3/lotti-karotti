package com.example.lottikarotti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private Button carrotButton;
    private Button drawButton;
    private Button moveTurn;
    private Button endTurn;
    private ImageView cardView;
    private ImageView hole;
    private boolean myTurn;
    private int touchcounter;
    private int touchCntLimit;

    private ImageView gameboard;
    private ImageView figOne;

    private float corX, corY, radius;

    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = {
            R.id.hole1, R.id.hole2, R.id.hole7 };

    @SuppressLint({"ClickableViewAccessibility", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carrotButton= (Button) findViewById(R.id.carrotButton);
        cardView = (ImageView) findViewById(R.id.imageViewCard);
        drawButton = (Button) findViewById(R.id.drawCard);
        moveTurn = (Button) findViewById(R.id.moveTurn);
        moveTurn.setEnabled(false);
        gameboard = (ImageView) findViewById(R.id.imageView);
        figOne = (ImageView) findViewById(R.id.figOne);
        endTurn = (Button) findViewById(R.id.endTurn);
        endTurn.setEnabled(false);
        myTurn = false;
        touchcounter = 0;
        touchCntLimit = -1;
        corX = -1; corY = -1;
        radius = 180;
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

                int random = ThreadLocalRandom.current().nextInt(0, 4);
                cardView.setImageResource(cards[random]);
                switch(random) {
                    case 0: touchCntLimit = 3; moveTurn.setEnabled(true); break;
                    case 1: boolean pressCarrot = true; break;
                    case 2: touchCntLimit = 1; moveTurn.setEnabled(true); break;
                    case 3: touchCntLimit = 2; moveTurn.setEnabled(true); break;
                }

                /*if(random == 1){
                    boolean pressCarrot = true;
                    carrotButton.setEnabled(pressCarrot);
                }*/

            }
        });




        moveTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTurn = true;
                moveTurn.setEnabled(false);
                endTurn.setEnabled(false);
                drawButton.setEnabled(false);
                touchcounter = 0;
                gameboard.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN && myTurn && touchcounter < touchCntLimit) {
                            float x = event.getX();
                            float y = event.getY();
                            if (corX == -1 && corY == -1 || isWithinRadius(x, y)) {
                                animateFigure(x, y);
                                touchcounter++;
                                corX = x;
                                corY = y;
                            }

                        } else if (touchcounter >= touchCntLimit) {
                            endTurn.setEnabled(true);
                            drawButton.setEnabled(false);
                        }
                        return true;
                    }
                });

            }

        });
        endTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myTurn = false;
                drawButton.setEnabled(true);
                endTurn.setEnabled(false);
            }
        });

    }

    private void animateFigure(float x, float y) {
        figOne.animate()
                .x(x - (figOne.getWidth() / 2))
                .y(y - (figOne.getHeight() / 2))
                .setDuration(500)
                .start();
    }
    private boolean isWithinRadius(float x, float y) {
        float distance = (float) Math.sqrt(Math.pow(x - corX, 2) + Math.pow(y - corY, 2));
        return distance <= radius;
    }
}