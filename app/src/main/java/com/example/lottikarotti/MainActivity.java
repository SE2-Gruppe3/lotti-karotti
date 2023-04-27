package com.example.lottikarotti;

import static com.example.lottikarotti.Network.ServerConnection.checkIfConnectionIsAlive;
import static com.example.lottikarotti.Network.ServerConnection.createNewLobby;
import static com.example.lottikarotti.Network.ServerConnection.getListOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getNumberOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getSocket;
import static com.example.lottikarotti.Network.ServerConnection.registerNewPlayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Console;
import java.util.Random;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity {
    private Button carrotButton;

    //  Socket instance
     private static final Socket socket = getSocket();
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
    final int[]rabbits={
          R.id.rabbit1,R.id.rabbit2, R.id.rabbit3, R.id.rabbit4};
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = {
       R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};

    final int[] fields = {    R.id.buttonField1,
            R.id.buttonField1, R.id.buttonField2,R.id.buttonField3,R.id.buttonField4,R.id.buttonField5,R.id.buttonField6,R.id.buttonField7,
            R.id.buttonField8,R.id.buttonField9,R.id.buttonField10, R.id.buttonField11, R.id.buttonField12, R.id.buttonField13, R.id.buttonField14,
    R.id.buttonField15, R.id.buttonField16, R.id.buttonField17, R.id.buttonField18, R.id.buttonField19, R.id.buttonField20,
    R.id.buttonField21, R.id.buttonField22, R.id.buttonField23, R.id.buttonField24,R.id.buttonField25,R.id.buttonField26,R.id.buttonField27, R.id.buttonField28,R.id.buttonField29};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        socket.emit("register", "abc");
        socket.emit("createlobby", "123456");
        socket.emit("playonline");

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
        carrotButton.setEnabled(false);


        instructions.setText("Instructions: Choose a rabbit to play");

        gameBoard = (ImageView) findViewById(R.id.imageView);
        figOne = (ImageView) findViewById(R.id.rabbit1);
        myTurn = false;
        touchCounter = 0;
        touchCntLimit = -1;
        corX = -1; corY = -1;
        radius = 180;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        socket.on("move", args -> {
            System.out.println(args[0].toString()+ " -- "+args[1].toString());
            handleMove((String) args[0], (int)args[1]);
                });

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
                drawButton.setEnabled(true);
                Random rand = new Random();
                int random = rand.nextInt(10);
                for (int hole : holes) {
                    ImageView img = (ImageView) findViewById(hole);
                    img.setVisibility(View.GONE);

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
                    case 0: drawButton.setEnabled(false); instructions.setText("Instructions: Move three fields with your rabbit on the game board"); playerMove(3); break;
                    case 1: carrotButton.setEnabled(true);drawButton.setEnabled(false); instructions.setText("Instructions: Click the carrot on the game board");
                        break;
                    case 2:  drawButton.setEnabled(false);instructions.setText("Instructions: Move one field with your rabbit on the game board");playerMove(1); break;
                    case 3:  drawButton.setEnabled(false);instructions.setText("Instructions: Move two fields with your rabbit on the game board");playerMove(2); break;
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


    }


    /**
     * This method sends an emit to the Server signalising "move"
     * @param steps
     */
    private void playerMove(int steps){
        
    }

    private void handleMove(String socketID, int steps){
        socket.emit("move", steps);
    }
    private void moveOn(User u, int steps) {

        // Disable the draw button
        drawButton.setEnabled(false);

        // Listen for the move event from the server
        socket.on("move", args -> {

            Log.d("move", "move");
            // Extract the steps from the event arguments
            int moveSteps = (int) args[1];
            Log.d("move", "arg passed");

            // Update the current field of the rabbit
            int currentField = u.getCurrentRabbit().getField() + moveSteps;
            u.getCurrentRabbit().setField(currentField);

            // Get the target button and enable it

            Button targetButton = findViewById(fields[currentField]);
            runOnUiThread(()->{{
                targetButton.setEnabled(true);
            }
            });

            targetButton.setVisibility(View.VISIBLE);

            // Set the onClickListener for the target button
            targetButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Get the coordinates of the button
                    int[] values = new int[2];
                    v.getLocationOnScreen(values);
                    float x = values[0];
                    float y = values[1];

                    // Animate the rabbit figure
                    animateFigure(x, y, u);

                    // Update the x and y coordinates of the rabbit
                    u.getCurrentRabbit().setxCor(x);
                    u.getCurrentRabbit().setyCor(y);

                    // Enable the draw button and disable the target button
                    drawButton.setEnabled(true);
                    targetButton.setEnabled(false);

                    // Set the instruction text
                    instructions.setText("Draw card or choose rabbit");
                }
            });
        });

        // Emit the movement to the server
        socket.emit("move", steps);
    }

    private void animateFigure(float x, float y,User u) {
        ImageView currentRabbit =(ImageView) findViewById(rabbits[u.getCurrentRabbit().getId()-1]);
        currentRabbit.animate()
                .x(x - (currentRabbit.getWidth()/2 )+50)
                .y(y - (currentRabbit.getHeight() / 2))
                .setDuration(500)
                .start();

        currentRabbit.clearAnimation();
    }


}