package com.example.lottikarotti;

import static com.example.lottikarotti.Network.ServerConnection.checkIfConnectionIsAlive;
import static com.example.lottikarotti.Network.ServerConnection.createNewLobby;
import static com.example.lottikarotti.Network.ServerConnection.getListOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getNumberOfConnectedPlayers;
import static com.example.lottikarotti.Network.ServerConnection.getSocket;
import static com.example.lottikarotti.Network.ServerConnection.registerNewPlayer;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

import io.socket.client.Socket;

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
    final int[] rabbits = {
            R.id.rabbit1, R.id.rabbit2, R.id.rabbit3, R.id.rabbit4};
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4};
    final int[] holes = {
            R.id.hole3, R.id.hole5, R.id.hole7, R.id.hole9, R.id.hole12, R.id.hole17, R.id.hole19,
            R.id.hole22, R.id.hole25, R.id.hole27};

    final int[] fields = {R.id.buttonField1,
            R.id.buttonField1, R.id.buttonField2, R.id.buttonField3, R.id.buttonField4, R.id.buttonField5, R.id.buttonField6, R.id.buttonField7,
            R.id.buttonField8, R.id.buttonField9, R.id.buttonField10, R.id.buttonField11, R.id.buttonField12, R.id.buttonField13, R.id.buttonField14,
            R.id.buttonField15, R.id.buttonField16, R.id.buttonField17, R.id.buttonField18, R.id.buttonField19, R.id.buttonField20,
            R.id.buttonField21, R.id.buttonField22, R.id.buttonField23, R.id.buttonField24, R.id.buttonField25, R.id.buttonField26, R.id.buttonField27, R.id.buttonField28, R.id.buttonField29};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Socket socket = getSocket();

        checkIfConnectionIsAlive(socket, this);
        getNumberOfConnectedPlayers(socket, this);
        registerNewPlayer(socket, "Robot");
        createNewLobby(socket, 1234567);
        getListOfConnectedPlayers(socket, this);
        rabbit1 = (ImageView) findViewById(R.id.rabbit1);
        rabbit2 = (ImageView) findViewById(R.id.rabbit2);
        rabbit3 = (ImageView) findViewById(R.id.rabbit3);
        rabbit4 = (ImageView) findViewById(R.id.rabbit4);
        instructions = (TextView) findViewById(R.id.textViewInstructions);


        for (int field : fields) {
            Button button = (Button) findViewById(field);
            button.setEnabled(false);
        }

        User user = new User("testuserl", new Rabbit(1, rabbit1.getLeft(), rabbit1.getRight()), new Rabbit(2, rabbit2.getLeft(), rabbit2.getRight()), new Rabbit(3, rabbit3.getLeft(), rabbit3.getRight()), new Rabbit(4, rabbit4.getLeft(), rabbit4.getRight()));
        carrotButton = (Button) findViewById(R.id.carrotButton);
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
        corX = -1;
        corY = -1;
        radius = 180;
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();


        rabbit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit1());
                user.getRabbit1().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit" + user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit4());
                user.getRabbit4().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit" + user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit3());
                user.getRabbit3().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit" + user.getCurrentRabbit().getId());
                drawButton.setEnabled(true);
            }
        });
        rabbit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                user.setCurrentRabbit(user.getRabbit2());
                user.getRabbit2().setInUse(true);
                instructions.setText("Instructions: You are playing with Rabbit" + user.getCurrentRabbit().getId());
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
                ImageView img = (ImageView) findViewById(holes[random]);
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

                switch (random) {
                    case 0:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move three fields with your rabbit on the game board");
                        moveOn(user, 3);
                        break;
                    case 1:
                        carrotButton.setEnabled(true);
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Click the carrot on the game board");
                        break;
                    case 2:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move one field with your rabbit on the game board");
                        moveOn(user, 1);
                        break;
                    case 3:
                        drawButton.setEnabled(false);
                        instructions.setText("Instructions: Move two fields with your rabbit on the game board");
                        moveOn(user, 2);
                        break;
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


    private void moveOn(User u, int step) {


        drawButton.setEnabled(false);

        int currentField = u.getCurrentRabbit().getField() + step;
      /*  if(u.getRabbit2().getField() ==currentField && u.getRabbit3().getField() == currentField && u.getRabbit4().getField()== currentField)
        {
            instructions.setText("Please choose another rabbit");
            return;
        }*/
        u.getCurrentRabbit().setField(currentField);

        Button targetButton = (Button) findViewById(fields[currentField]);
        targetButton.setEnabled(true);
        // targetButton.setBackgroundResource(R.drawable.deckkarte);
        targetButton.setVisibility(View.VISIBLE);

        targetButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                int[] values = new int[2];
                v.getLocationOnScreen(values);
                float x = values[0];
                float y = values[1];
                animateFigure(x, y, u);
                u.getCurrentRabbit().setxCor(x);
                u.getCurrentRabbit().setyCor(y);
                drawButton.setEnabled(true);
                targetButton.setEnabled(false);
                instructions.setText("Draw card or choose rabbit");
            }
        });
    }

    private void animateFigure(float x, float y, User u) {
        ImageView currentRabbit = (ImageView) findViewById(rabbits[u.getCurrentRabbit().getId() - 1]);
        currentRabbit.animate()
                .x(x - (currentRabbit.getWidth() / 2) + 50)
                .y(y - (currentRabbit.getHeight() / 2) - 60)
                .setDuration(500)
                .start();

        currentRabbit.clearAnimation();

    }

    @Override
    public void onDestroy() {
//       Intent intent = new Intent(this, BGMusic.class);
//        stopService(intent);
        super.onDestroy();

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateBrightness();
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }

}