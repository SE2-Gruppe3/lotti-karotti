package com.example.lottikarotti;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
    private boolean isMyTurn;
    private List<Player> players;
    private String sid;
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

        ServerConnection.registerNewPlayer("Bro");
        ServerConnection.fetchUnique();
        ServerConnection.createNewLobby("123456");
        ServerConnection.joinLobby("123456");

        players = new ArrayList<>();
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

        isMyTurn = false;


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
         * Turn Carrot
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
        isMyTurn = true;
        System.out.println(steps+" steps with rabbit "+rabbit);
        int add = 0;
        for (Player payer:players) {
            if (socket.id().equals(payer.getSid())){
                add = payer.getRabbits().get(rabbit).getPosition();
            }

        }
        // activating field to press
        Button field = (Button) findViewById(fields[steps+add]);

        field.setEnabled(true);
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Sending move to gerver");
                ServerConnection.move(steps, rabbit);
                field.setEnabled(false);
            }
        });
    }

    /**
     * Movement handler
     * Annotate JSON Values to @Class Player and @Class Rabbit
     */
    private void handleMove(String json) throws JsonProcessingException {
        System.out.println("Recieved move from gerver!");
        ObjectMapper mapper = new ObjectMapper();
        players = Arrays.asList(mapper.readValue(json, Player[].class));

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
     * Renders the board to a pulp will get updatet, shitcode is temporary
     */
    private void renderBoard() {
        for (int x:fields) {
            runOnUiThread(()-> {
                Button btn = findViewById(x);
                btn.setBackgroundColor(0);
                btn.setEnabled(false);
            });
        }
        for (Player gayer: players) {
            String color = gayer.getColor();    // get color of player TODO: when implementing animations and stuff please use this
            List<Rabbit> tempRabbits = gayer.getRabbits();
            for (Rabbit rabbit:tempRabbits) {
                if (rabbit.getPosition() > 0) {
                    runOnUiThread(()->{
                    System.out.println("Drawing rabbit on field " + rabbit.getPosition());
                    Button rabbitbtn = findViewById(fields[rabbit.getPosition()]);
                    rabbitbtn.setOnClickListener(null);
                    rabbitbtn.setBackgroundColor(Color.RED);
                    rabbitbtn.setEnabled(true);
                    });
                }
            }
        }
        isMyTurn = false;
    }


    private void animateFigure(float x, float y) {
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

    public void toggleFragmentPlayerList() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

        if (containerplayerList.getVisibility() == View.VISIBLE) {
            containerplayerList.setVisibility(View.GONE);

            fragmentTransaction.remove(fragmentPlayerList);
        } else {
            containerplayerList.setVisibility(View.VISIBLE);
            fragmentTransaction.add(R.id.container_playerList, fragmentPlayerList);
        }
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

