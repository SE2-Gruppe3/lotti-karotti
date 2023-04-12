package com.example.lottikarotti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Matrix;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class MainActivity extends AppCompatActivity {

    private Button carrotButton;
    private Button drawButton;
    private Button startTurn;
    private Button endTurn;
    private ImageView cardView;
    private ImageView hole;
    private ImageView gameBoard;
    private ImageView figOne;
    private float corX, corY, radius;
    private boolean myTurn;
    private int touchCounter;
    private int touchCntLimit;
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = {
       R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        carrotButton= (Button) findViewById(R.id.carrotButton);
        cardView = (ImageView) findViewById(R.id.imageViewCard);
        drawButton = (Button) findViewById(R.id.drawCard);
        startTurn = (Button) findViewById(R.id.moveTurn);
        startTurn.setEnabled(false);

        gameBoard = (ImageView) findViewById(R.id.imageView);
        figOne = (ImageView) findViewById(R.id.figOne);
        endTurn = (Button) findViewById(R.id.endTurn);
        endTurn.setEnabled(false);
        myTurn = false;
        touchCounter = 0;
        touchCntLimit = -1;
        corX = -1; corY = -1;
        radius = 180;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        carrotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int random = rand.nextInt(10);
                ImageView img=(ImageView)findViewById(holes[random]);
                img.setVisibility(View.VISIBLE);
                boolean carrotClicked = false;
                carrotButton.setEnabled(carrotClicked);

            }
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int random = rand.nextInt(4);
                cardView.setImageResource(cards[random]);
                switch(random) {
                    case 0: touchCntLimit = 3; startTurn.setEnabled(true); break;
                    case 1: boolean pressCarrot = true; break;
                    case 2: touchCntLimit = 1; startTurn.setEnabled(true); break;
                    case 3: touchCntLimit = 2; startTurn.setEnabled(true); break;
                }



            }
        });




        startTurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                myTurn = true;
                startTurn.setEnabled(false);
                endTurn.setEnabled(false);
                drawButton.setEnabled(false);
                touchCounter = 0;
                gameBoard.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {
                        if (event.getAction() == MotionEvent.ACTION_DOWN && myTurn && touchCounter < touchCntLimit) {
                            float x = event.getX();
                            float y = event.getY();
                            if (corX == -1 && corY == -1 || isWithinRadius(x, y)) {
                                animateFigure(x, y);
                                touchCounter++;
                                corX = x;
                                corY = y;
                            }

                        } else if (touchCounter >= touchCntLimit) {
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