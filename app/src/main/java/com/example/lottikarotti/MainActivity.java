package com.example.lottikarotti;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lottikarotti.Listeners.IOnDataSentListener;
import com.example.lottikarotti.Network.ServerConnection;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity implements IOnDataSentListener, SensorEventListener {
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
    private int currRabbit;
    private final String TAG = "MainActivity";

    //  Container for the Player List Fragment (Placeholder Container)
    private FrameLayout containerplayerList;
    private Fragment fragmentPlayerList;

    private TextView instructions;

    // Variables for the Sensor
    private SensorManager sensorManager;
    private Sensor shakeSensor;
    private float oldX, oldY, oldZ;
    private long preUpdate;
    private static final int SHAKE_THRESHOLD = 1000;
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

    private Socket socket;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            socket = ServerConnection.getInstance("http://192.168.178.22:3000");
            ServerConnection.connect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ServerConnection.registerNewPlayer("Amar");
        ServerConnection.createNewLobby("123456");
        ServerConnection.joinLobby("123456");


        /// Example of getting server response using callbacks - We get here online player count back
        ServerConnection.getNumberOfConnectedPlayers(this, new ServerConnection.PlayerCountCallback() {
            @Override
            public void onPlayerCountReceived(int count) {
                Toast.makeText(getApplicationContext(), "Online players: " + count, Toast.LENGTH_SHORT).show();
            }
        });

        /** Initialize Sensor**/
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);


        rabbit1 = (ImageView) findViewById(R.id.rabbit1);
        rabbit2 = (ImageView) findViewById(R.id.rabbit2);
        rabbit3 = (ImageView) findViewById(R.id.rabbit3);
        rabbit4 = (ImageView) findViewById(R.id.rabbit4);
        instructions= (TextView) findViewById(R.id.textViewInstructions);

        //  Initialize PlayerList Fragment and Layout
        containerplayerList = findViewById(R.id.container_playerList);
        fragmentPlayerList = new PlayerListFragment();


        for (int field : fields) {
           Button button= (Button)findViewById(field);
           button.setEnabled(false);
        }

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
            try {
                handleMove(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle move \n" + e.toString());
            }
                });

        socket.on("shake", args -> {
            try {
                handleShake(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle shake \n" + e.toString());
            }
        });

        /**
         * Rabbit Selection
         */
        rabbit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(0);
            }
        });
        rabbit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(1);
            }
        });
        rabbit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(2);
            }
        });
        rabbit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(3);
            }
        });

        /**
         * Settings Button
         */
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
                    case 0: drawButton.setEnabled(false); instructions.setText("Instructions: Move three fields with your rabbit on the game board"); playerMove(3, currRabbit); break;
                    case 1: carrotButton.setEnabled(true);drawButton.setEnabled(false); instructions.setText("Instructions: Click the carrot on the game board");
                        break;
                    case 2:  drawButton.setEnabled(false);instructions.setText("Instructions: Move one field with your rabbit on the game board");playerMove(1, currRabbit); break;
                    case 3:  drawButton.setEnabled(false);instructions.setText("Instructions: Move two fields with your rabbit on the game board");playerMove(2, currRabbit); break;
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
     * Override the onSensorChanged method to detect the shake gesture
     */
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    /**
     * This method sends an emit to the Server signalising "move"
     * @param steps
     */
    private void playerMove(int steps, int rabbit){
        System.out.println(steps+" steps with");

        // send the steps aswell as the rabbit to the server (the server can fetch the socketid itself)
        ServerConnection.move(steps, rabbit);
    }

    /**
     * Temporary solution for Movement handling
     */
    private void handleMove(String json) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        List<Player> players = Arrays.asList(mapper.readValue(json, Player[].class));

        renderBoard();
    }
    /**
     * Handle the shake event
     * @param socketid
     */
    private void handleShake(String socketid)  {
        if (socketid != socket.id()){

        }
    }

    /**
     * Implement here
     */
    private void renderBoard(){
        //TODO: Implement
    }

    /*
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

     */

    private void animateFigure(float x, float y,User u) {
        ImageView currentRabbit =(ImageView) findViewById(rabbits[currRabbit-1]);
        currentRabbit.animate()
                .x(x - (currentRabbit.getWidth()/2 )+50)
                .y(y - (currentRabbit.getHeight() / 2))
                .setDuration(500)
                .start();

        currentRabbit.clearAnimation();
    }

    public void openFragmentPlayerList(View view){
        toggleFragmentPlayerList();
    }

    public void toggleFragmentPlayerList(){
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        int visibility = containerplayerList.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE;
        containerplayerList.setVisibility(visibility);

        fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.add(R.id.container_playerList, fragmentPlayerList, visibility == View.VISIBLE ? "player_list" : null);
        fragmentTransaction.commit();
    }

    @Override
    public void onDataSent(String data) {
        Log.d("Game", "Received data from Fragment: " + data);

        if ("closeFragmentPlayerList".equals(data))
            toggleFragmentPlayerList();
    }

    public void selectRabbit(int num){
        if (num<4) {
            currRabbit = num;
            instructions.setText("Instructions: You are playing with Rabbit"+currRabbit);
            drawButton.setEnabled(true);
        }
        else
            instructions.setText("Fuck you");
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float newX = sensorEvent.values[0];
            float newY = sensorEvent.values[1];
            float newZ = sensorEvent.values[2];

            long timeNow = System.currentTimeMillis();

            if ((timeNow - preUpdate) > 100) {
                long diff = (timeNow - preUpdate);
                preUpdate = timeNow;

                float speed = Math.abs(newX + newY + newZ - oldX - oldY - oldZ) / diff * 10000;

                if (speed > SHAKE_THRESHOLD) {
                    onShakeDetected();
                }

                oldX = newX;
                oldY = newY;
                oldZ = newZ;
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
    private void onShakeDetected() {
        ServerConnection.shake();
        Toast.makeText(this, "Shake detected!", Toast.LENGTH_SHORT).show();
    }

}

