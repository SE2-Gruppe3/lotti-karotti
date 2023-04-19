package com.example.lottikarotti;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private Button carrotButton;

    private ImageButton settingsButton;
    private Button drawButton;
    private Button startTurn;
    private Button endTurn;
    private ImageView cardView;
   private ImageView rabbit1;
    private ImageView rabbit2;
    private ImageView rabbit3;
    private ImageView rabbit4;

    private ImageView gameBoard;
    private ImageView figOne;
    private float corX, corY, radius;
    private boolean myTurn;
    private int touchCounter;
    private int touchCntLimit;

    private TextView instructions;
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = {
       R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};

    final int[] fields = {
            R.id.buttonField1, R.id.buttonField2,R.id.buttonField3,R.id.buttonField4,R.id.buttonField5,R.id.buttonField6,R.id.buttonField7,
            R.id.buttonField8,R.id.buttonField9,R.id.buttonField10, R.id.buttonField11, R.id.buttonField12, R.id.buttonField13, R.id.buttonField14,
    R.id.buttonField15, R.id.buttonField16, R.id.buttonField17, R.id.buttonField18, R.id.buttonField19, R.id.buttonField20,
    R.id.buttonField21, R.id.buttonField22, R.id.buttonField23, R.id.buttonField24,R.id.buttonField25,R.id.buttonField26,R.id.buttonField27, R.id.buttonField28,R.id.buttonField29};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rabbit1 = (ImageView) findViewById(R.id.rabbit1);
        rabbit2 = (ImageView) findViewById(R.id.rabbit2);
        rabbit3 = (ImageView) findViewById(R.id.rabbit3);
        rabbit4 = (ImageView) findViewById(R.id.rabbit4);
        instructions= (TextView) findViewById(R.id.textViewInstructions);



        for (int field : fields) {
           Button button= (Button)findViewById(field);
           button.setEnabled(false);
        }

        User user = new User("testuserl", new Rabbit(1,rabbit1.getLeft(),rabbit1.getRight()), new Rabbit(2,rabbit2.getLeft(),rabbit2.getRight()),new Rabbit(3,rabbit3.getLeft(),rabbit3.getRight()), new Rabbit(4,rabbit4.getLeft(),rabbit4.getRight()));
        carrotButton= (Button) findViewById(R.id.carrotButton);
        cardView = (ImageView) findViewById(R.id.imageViewCard);
        settingsButton = (ImageButton) findViewById(R.id.settings);
        drawButton = (Button) findViewById(R.id.drawCard);
        drawButton.setEnabled(false);
        startTurn = (Button) findViewById(R.id.moveTurn);
        startTurn.setEnabled(false);

        instructions.setText("Instructions: Choose a rabbit to play");

        gameBoard = (ImageView) findViewById(R.id.imageView);
        figOne = (ImageView) findViewById(R.id.rabbit1);
        endTurn = (Button) findViewById(R.id.endTurn);
        endTurn.setEnabled(false);
        myTurn = false;
        touchCounter = 0;
        touchCntLimit = -1;
        corX = -1; corY = -1;
        radius = 180;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        rabbit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit1());
                user.getRabbit1().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit"+user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit4());
                user.getRabbit4().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit"+user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit3());
                user.getRabbit3().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit"+user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit2());
                user.getRabbit2().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit"+user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        carrotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int random = rand.nextInt(10);
                for (int hole : holes) {
                    ImageView img = (ImageView) findViewById(hole);
                    img.setVisibility(View.INVISIBLE);
                }
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
                    case 0: touchCntLimit = 3;instructions.setText("Instructions: Move three fields with\n your rabbit on the game board"); startTurn.setEnabled(true); break;
                    case 1: carrotButton.setEnabled(true); instructions.setText("Instructions: Click the carrot\n on the game board");
                        break;
                    case 2: touchCntLimit = 1; instructions.setText("Instructions: Move one field with\n your rabbit on the game board");moveOnStep(user);startTurn.setEnabled(true); break;
                    case 3: touchCntLimit = 2; instructions.setText("Instructions: Move two fields with\n your rabbit on the game board");startTurn.setEnabled(true); break;
                }
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });




        startTurn.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ClickableViewAccessibility")
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


    private void moveOnStep( User u){
     //   Rabbit currRabbit = u.getCurrentRabbit();
      //  if(currRabbit.getField() == 0){

       // }
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