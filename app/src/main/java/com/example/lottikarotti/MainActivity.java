package com.example.lottikarotti;

import org.apache.commons.lang3.ArrayUtils;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PointF;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;

import android.view.WindowManager;
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

    private String lobbyId;
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

    //Variables for the Clouds
    private Button cloudButton;
    private int screenWidth;
    private int screenHeight;
    private ImageView cloudL;
    private ImageView cloudR;
    private int cloudLX;
    private int cloudRX;

    //--------------------------------
    private boolean isMyTurn;
    private boolean isCheating;
    private List<Player> players;
    private String sid;
    final int[]rabbits={
            R.id.rabbit1,R.id.rabbit2, R.id.rabbit3, R.id.rabbit4};


    PointF[] rabbitStartPos = new PointF[8];
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4 };
    final int[] holes = { R.id.hole0,
            R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};
    private static int currHole = -1;

    final int[] fields = { R.id.buttonField1,
            R.id.buttonField1, R.id.buttonField2,R.id.buttonField3,R.id.buttonField4,R.id.buttonField5,R.id.buttonField6,R.id.buttonField7,
            R.id.buttonField8,R.id.buttonField9,R.id.buttonField10, R.id.buttonField11, R.id.buttonField12, R.id.buttonField13, R.id.buttonField14,
            R.id.buttonField15, R.id.buttonField16, R.id.buttonField17, R.id.buttonField18, R.id.buttonField19, R.id.buttonField20,
            R.id.buttonField21, R.id.buttonField22, R.id.buttonField23, R.id.buttonField24,R.id.buttonField25,R.id.buttonField26,R.id.buttonField27, R.id.buttonField28,R.id.buttonField29};

    private Socket socket;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DisplayMetrics displayMetrics = new DisplayMetrics();

        try {
            socket = ServerConnection.getInstance("http://10.0.0.6:3000");
            ServerConnection.connect();
            Log.d(TAG, "onCreate: Connected to server");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        Intent intent = getIntent();
        lobbyId = intent.getStringExtra("lobbyId");
        String username = intent.getStringExtra("username");
        String info = intent.getStringExtra("info");

        TextView lobbyID = findViewById(R.id.lobbyID);
        lobbyID.setText("Lobby ID: " + lobbyId);

        ServerConnection.registerNewPlayer(username);
        ServerConnection.fetchUnique();
        if(info.equals("start")){
            ServerConnection.createNewLobby(lobbyId);
            ServerConnection.joinLobby(lobbyId);
            Log.d(TAG, "onCreate: Created new lobby" + lobbyId);
        }
        else{
            ServerConnection.joinLobby(lobbyId);
        }

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

       // isMyTurn = false;

        rabbit1 = (ImageView) findViewById(R.id.rabbit1);
        rabbit2 = (ImageView) findViewById(R.id.rabbit2);
        rabbit3 = (ImageView) findViewById(R.id.rabbit3);
        rabbit4 = (ImageView) findViewById(R.id.rabbit4);

        rabbit1.setImageResource(R.drawable.fig11);
        rabbit2.setImageResource(R.drawable.fig11);
        rabbit3.setImageResource(R.drawable.fig11);
        rabbit4.setImageResource(R.drawable.fig11);

        instructions= (TextView) findViewById(R.id.textViewInstructions);

        //  Initialize PlayerList Fragment and Layout
        containerplayerList = findViewById(R.id.container_playerList);
        fragmentPlayerList = new PlayerListFragment();

        //Disale all fields at the start of the game
        for (int field : fields) {
            ImageButton button= (ImageButton)findViewById(field);
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
        corX = -1;
        corY = -1;
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
        socket.on("moveCheat", args -> {
            try {
                handleMove(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle move cheat \n" + e.toString());
            }
        });

        socket.on("shake", args -> {
            Log.println(Log.INFO, "Shake", "Shake received");
            try {
                handleShake(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle shake \n" + e.toString());
            }
        });
        socket.on("cheat", args -> {
            Log.println(Log.INFO, "Cheat", "CHEAT received");
            try {
                handleCheating(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't cheating \n" + e.toString());
            }
        });

        socket.on("carrotspin", args -> {
            Log.println(Log.INFO, "Carrot", "carrotspin received");
            try {
                handleCarrotspin(args[0].toString(), args[1].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle carrotspin \n" + e.toString());
            }
        });

        socket.on("gethole", args -> {
            Log.println(Log.INFO, "Hole", "gethole received");
            try {
                handleGetHole(args[0].toString(), args[1].toString(), args[2].toString(), args[3].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle gethole \n" + e.toString());
            }
        });

        socket.on("turn", id -> {
            Log.println(Log.INFO, "Turn", "Turn received" +id[0].toString()+"<-gerver - l0cal->"+socket.id().toString());
            if (id[0].toString().equals(socket.id().toString())) setMyTurn(true);

        });

        /**
         * Clouds for the Sensor
         */
        cloudL = (ImageView) findViewById(R.id.cloudLeft);
        cloudR = (ImageView) findViewById(R.id.cloudRight);
        // Get Starting-Location of Clouds

        int[] locationOfCloudL = new int[2];
        int[] locationOfCloudR = new int[2];
        cloudL.getLocationOnScreen(locationOfCloudL);
        cloudR.getLocationOnScreen(locationOfCloudR);
        cloudLX = locationOfCloudL[0];
        cloudRX = locationOfCloudR[0];

        // Get Screen Size
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;


        /**
         * Set Card Size based on Screen
         */
        ViewGroup.LayoutParams cloudLeftParam = cloudL.getLayoutParams();
        ViewGroup.LayoutParams cloudRightParam = cloudR.getLayoutParams();

        cloudLeftParam.width = screenWidth*2;
        cloudLeftParam.height = screenHeight/2;
        cloudL.setLayoutParams(cloudLeftParam);

        cloudRightParam.width = screenWidth * 2;
        cloudRightParam.height = screenHeight / 2;
        cloudR.setLayoutParams(cloudRightParam);





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

        // Toggle Player rabbits (disable if not own turn initially)
        togglePlayerRabbits();


        /**
         * Turn Carrot
         */
        carrotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawButton.setEnabled(true);

                ServerConnection.carrotSpin(lobbyId);

                carrotButton.setEnabled(false);

            }
        });

        drawButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Random rand = new Random();
                int random = rand.nextInt(4);
                cardView.setImageResource(cards[random]);
                instructions.setTextColor(Color.BLACK);

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

        setMyTurn(false);
    }


    private void setColorForRabbits() {
        Log.d("Rabbit", "setColorForRabbits: " + players.size());
        for (Player players : players) {
            if (players.getSid().equals(socket.id())) {
                switch (players.getColor()) {
                    case "white":
                        rabbit1.setImageResource(R.drawable.fig11);
                        rabbit2.setImageResource(R.drawable.fig11);
                        rabbit3.setImageResource(R.drawable.fig11);
                        rabbit4.setImageResource(R.drawable.fig11);
                        Log.d("Rabbit", "setColorForRabbits: " + players.getColor());
                        break;
                    case "red":
                        rabbit1.setImageResource(R.drawable.fig88);
                        rabbit2.setImageResource(R.drawable.fig88);
                        rabbit3.setImageResource(R.drawable.fig88);
                        rabbit4.setImageResource(R.drawable.fig88);
                        break;
                    case "pink":
                        rabbit1.setImageResource(R.drawable.fig22);
                        rabbit2.setImageResource(R.drawable.fig22);
                        rabbit3.setImageResource(R.drawable.fig22);
                        rabbit4.setImageResource(R.drawable.fig22);
                        break;
                    case "green":
                        rabbit1.setImageResource(R.drawable.fig77);
                        rabbit2.setImageResource(R.drawable.fig77);
                        rabbit3.setImageResource(R.drawable.fig77);
                        rabbit4.setImageResource(R.drawable.fig77);
                        break;
                }

            }
        }

    }



    /**
     * Override the onSensorChanged method to detect the shake gesture
     */
    @Override
    protected void onResume() {
        super.onResume();
        if(!myTurn) {
            sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
        updateBrightness();
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
        if (!isMyTurn) return;

        System.out.println(steps+" steps with rabbit "+rabbit);
        int add = 0;
        for (Player payer:players) {
            if (socket.id().equals(payer.getSid())){
                add = payer.getRabbits().get(rabbit).getPosition();
            }

        }
        // activating field to press
        ImageButton field = (ImageButton) findViewById(fields[steps+add]);
        field.setEnabled(true);
        int puffer = steps+add;
        //int addPuff = add;
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Sending move to server");
                ImageButton fieldtest = (ImageButton) findViewById(fields[puffer]);
                int delay = 0;
//                while(fieldtest.getDrawable() != null){
//                    System.out.println("Field is taken, steps + 1");
//                    ++delay;
//                    fieldtest =findViewById(fields[puffer+delay]);
//                }
                final int finalDelay = delay;
                ServerConnection.getHole(lobbyId, puffer+finalDelay, rabbit);
//                if(checkForHoles(puffer+finalDelay)){
//                    Log.d("Hole", "onClick: " + finalDelay);
//                    ServerConnection.reset(addPuff);
//                    field.setEnabled(false);
//                } else {
//                    Log.d("Move", "onClick: " + finalDelay);
//                    ServerConnection.move(steps + finalDelay, rabbit);
                    field.setEnabled(false);
               // }
            }
        });
    }

    /**
     * Movement handler
     * Annotate JSON Values to @Class Player and @Class Rabbit
     */
    private void handleMove(String json) throws JsonProcessingException {
        System.out.println("Received move from server!");
        ObjectMapper mapper = new ObjectMapper();
        players = Arrays.asList(mapper.readValue(json, Player[].class));

        renderBoard();
    }
    /**
     * Handle the Cheating
     */
    private void handleCheating(String socketid){

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post( new Runnable() {

            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "here", Toast.LENGTH_SHORT).show();


            }
        });
    }
    private void handleGetHole(String json, String hole, String desired, String rabbit) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        players = Arrays.asList(mapper.readValue(json, Player[].class));


                //Toast.makeText(MainActivity.this, "here", Toast.LENGTH_SHORT).show();
              //  Log.d("Hole", "run: " + hole + " " + desired + " " + rabbit);
                int currhole = Integer.parseInt(hole);
                int desiredPos = Integer.parseInt(desired);
                int rabbitCurr = Integer.parseInt(rabbit);
                Log.d("Hole", "handlegethole: " + players.size());
                for(Player p: players){
                    Log.d("Hole", "Players");
                    if(p.getSid().equals(socket.id())){
                        Log.d("Hole", "Socket match");
                      //  runOnUiThread(() -> {
                            checkForHoles(p.getRabbits().get(rabbitCurr).getPosition(), currhole, desiredPos, rabbitCurr);
                    //    });

                        }
                    }
                }




    /**
     * Handle the shake event
     */
    private void handleShake(String socketid)  {

        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                //Toast.makeText(MainActivity.this, "Shake detected!", Toast.LENGTH_SHORT).show();
                if (!socketid.equals(socket.id())) {
                    animateClouds(screenWidth);
                    resetClouds(cloudLX, cloudRX);
                } else {
                    instructions.setText("You are now able to cheat, others cant see you !!");
                    instructions.setTextColor(Color.RED);

                    isCheating = true;
                    ServerConnection.cheat("Brooo");

                   /*  for (Player p: players) {
                        if (!(p.getSid().equals(socket.id()))) {
                            for (Rabbit r: p.getRabbits()) {

                            }
                        }
                    }*/
                    for (int i = 1; i < fields.length; i++) {
                        ImageButton field = (ImageButton) findViewById(fields[i]);
                        field.setEnabled(true);

                        field.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {

                                int position = ArrayUtils.indexOf(fields, field.getId()) ;
                                System.out.println("Sending move to server");
                                ImageButton fieldtest = (ImageButton) findViewById(fields[position]);
                                int delay = 0;
                                while (fieldtest.getDrawable() != null) {
                                    System.out.println("Field is taken, steps + 1");
                                    ++delay;
                                    fieldtest = findViewById(fields[delay+position]);
                                }
                                final int finalDelay = delay+position;
                                ServerConnection.getHole(lobbyId, finalDelay, currRabbit);
//                                if (checkForHoles(finalDelay)) {
//                                    Log.d("Hole", "onClick: " + finalDelay);
//                                    for (Player p: players) {
//                                        if (p.getSid().equals(socket.id())) {
//                                            ServerConnection.reset(p.getRabbits().get(currRabbit).getPosition());
//                                        }
//                                    }
//                                   // ServerConnection.reset();
                                    field.setEnabled(false);
//                                } else {
//                                    Log.d("Cheat Move", "onClick: " + finalDelay);
//                                    ServerConnection.cheatMove(finalDelay, currRabbit);
//                                    field.setEnabled(false);
//                                    isCheating=false;
//                                }
                            }

                        });

                    }
                    Toast.makeText(MainActivity.this, "Please choose field you want to move", Toast.LENGTH_LONG).show();

                }

            }
        });


    }

    /**
     * Handle Carrotclick (Carrotspin)
     */
    private void handleCarrotspin(String socketId, String number) throws JsonProcessingException {
        Log.d("Carrotspin", "Carrotspin received from server");
        String fieldid = "buttonfield"+number;
        Log.d("Carrotspin", "Field: "+fieldid);
        //hole = Integer.parseInt(number);
        Log.d("Carrotspin", "Hole: "+ number);
        putHolesOnBoard(Integer.parseInt(number));

    }


    /**
     * Renders the board to a pulp will get updated, shitcode is temporary
     */
    private void renderBoard() {
        for (int x:fields) {
            runOnUiThread(()-> {
                ImageButton btn = findViewById(x);
                btn.setBackgroundColor(0);
                btn.setImageResource(0);
                btn.setEnabled(false);
            });
        }
        Log.d("Rabbit", "Renderboard: " + players.size());
        for (Player gayer: players) {
            String color = gayer.getColor();    // get color of player TODO: when implementing animations and stuff please use this
            List<Rabbit> tempRabbits = gayer.getRabbits();
            Log.d("Rabbit", "Renderboard: Rabbit color: " + color);
            if(!(gayer.getSid().equals(socket.id()))) {
                for (Rabbit rabbit : tempRabbits) {
                    if (rabbit.getPosition() > 0) {
                        runOnUiThread(() -> {
                            ImageButton rabbitBtn = findViewById(fields[rabbit.getPosition()]);
                            rabbitBtn.setOnClickListener(null);
                            setColorForRabbitsRender(rabbitBtn, color);
                            rabbitBtn.setEnabled(false);
                        });
                    }
                }
            } else if (gayer.getSid().equals(socket.id())){
                for (Rabbit rabbit:tempRabbits) {
                    if (rabbit.getPosition() > 0) {
                        Log.d("Rabbit", "Renderboard.Rabbit: " + rabbit.getName());
                        runOnUiThread(()->{
                            System.out.println("Renderboard.Drawing rabbit on field " + rabbit.getPosition());
                            ImageButton rabbitbtn = findViewById(fields[rabbit.getPosition()]);
                            rabbitbtn.setOnClickListener(null);
                            setColorForRabbitsRender(rabbitbtn, color);
                            rabbitbtn.setEnabled(false);
                            //}
                        });
                    }
                }
            }
        }
        setMyTurn(false);
    }
    private void setColorForRabbitsRender(ImageButton rabbitbtn, String color) {
        switch (color) {
            case "white":
                rabbitbtn.setImageResource(R.drawable.fig11);
                Log.d("Rabbit", "setColorForRabbits: " + color);
                break;
            case "red":
                rabbitbtn.setImageResource(R.drawable.fig88);
                Log.d("Rabbit", "setColorForRabbits: " + color);
                break;
            case "pink":
                rabbitbtn.setImageResource(R.drawable.fig22);
                Log.d("Rabbit", "setColorForRabbits: " + color);
                break;
            case "green":
                rabbitbtn.setImageResource(R.drawable.fig77);
                Log.d("Rabbit", "setColorForRabbits: " + color);
                break;
        }

    }

    /**
     * Puts the holes on the board
     **/
    private void putHolesOnBoard(int holer) {
        runOnUiThread(()-> {
            for (int h : holes) {
                ImageView img = (ImageView) findViewById(h);
                img.setVisibility(View.INVISIBLE);
            }

            ImageView img=(ImageView)findViewById(holes[holer]);
            img.setVisibility(View.VISIBLE);
            checkForRabbit(holer);
            carrotButton.setEnabled(false);
            playerMove(0, 0);
            //renderBoard();
        });
    }

    private void checkForRabbit(int hole) {
        ImageButton puffer;
        switch (hole) {
            case 0:
                break;
            case 1:
                puffer = findViewById(fields[3]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(3);
                }
                break;
            case 2:
                puffer = findViewById(fields[5]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(5);
                }
                break;
            case 3:
                puffer = findViewById(fields[7]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(7);
                }
                break;
            case 4:
                puffer = findViewById(fields[9]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(9);
                }
                break;
            case 5:
                puffer = findViewById(fields[12]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(12);
                }
                break;
            case 6:
                puffer = findViewById(fields[17]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(17);
                }
                break;
            case 7:
                puffer = findViewById(fields[19]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(19);
                }
                break;
            case 8:
                puffer = findViewById(fields[22]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(22);
                }
                break;
            case 9:
                puffer = findViewById(fields[25]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(25);
                }
                break;
            case 10:
                puffer = findViewById(fields[27]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(27);
                }
                break;
        }
    }

    private void checkForHoles(int currPos, int currHole, int desiredPos, int rabbit){
        Log.d("Rabbit", "checkForHoles: " + currPos);
        if(desiredPos == 3 || desiredPos == 5 || desiredPos == 7 || desiredPos == 9 || desiredPos == 12 || desiredPos == 17 || desiredPos == 19 || desiredPos == 22 || desiredPos == 25 || desiredPos == 27) {
            switch (desiredPos) {
                case 3:
                    if (currHole == 1) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;

                case 5:
                    if (currHole == 2) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 7:
                    if (currHole == 3) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 9:
                    if (currHole == 4) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 12:
                    if (currHole == 5) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 17:
                    if (currHole == 6) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 19:
                    if (currHole == 7) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 22:
                    if (currHole == 8) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 25:
                    if (currHole == 9) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
                case 27:
                    if (currHole == 10) {
                        ServerConnection.reset(currPos);
                    } else {
                        Log.d("Move", "onClick: " + desiredPos);
                        ServerConnection.move(desiredPos-currPos, rabbit);
                    }
                    break;
            }
        } else {
            ServerConnection.move(desiredPos-currPos, rabbit);
        }
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
        if(!isMyTurn) {
            ServerConnection.shake();}

        //Debugging
        // animateClouds(screenWidth);
        //  handleShake("socketid");
    }

    private void animateClouds(Integer screenWidth) {
        float finalPosition = ((float)screenWidth);

        // Animate Left Cloud
        cloudL.animate()
                .translationX(finalPosition / 0.7f)
                .setDuration(2000)
                .start();

        // Animate Right Cloud
        cloudR.animate()
                .translationX(-finalPosition / 0.7f)
                .setDuration(2000)
                .start();

    }

    private void resetClouds(Integer cloudLX, Integer cloudRX) {

        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // Animate the clouds back to their initial positions after a 5 sec delay
                cloudL.animate()
                        .translationX(cloudLX)
                        .setDuration(1000)
                        .start();

                cloudR.animate()
                        .translationX(cloudRX)
                        .setDuration(1000)
                        .start();
            }
        }, 5000);
    }
    public void setMyTurn(boolean myTurn) {
        isMyTurn = myTurn;
        togglePlayerRabbits();
        Log.d("Game", "setMyTurn: " + isMyTurn);
    }
    private void togglePlayerRabbits() {
        Log.d("Game", "togglePlayerRabbits: " + isMyTurn);
        runOnUiThread(() -> {
            if (isMyTurn) {
                rabbit1.setEnabled(true);
                rabbit2.setEnabled(true);
                rabbit3.setEnabled(true);
                rabbit4.setEnabled(true);
            } else {
                drawButton.setEnabled(false);
                rabbit1.setEnabled(false);
                rabbit2.setEnabled(false);
                rabbit3.setEnabled(false);
                rabbit4.setEnabled(false);
            }
        });
    }

    private void getRabbitStartPos() {
        for (int i = 0; i < rabbits.length; i++) {
            int r = rabbits[i];
            ImageView rabbit = findViewById(r);
            int[] location = new int[2];
            rabbit.getLocationOnScreen(location);

            float centerX = location[0] + rabbit.getWidth() / 2.0f;
            float centerY = location[1] + rabbit.getHeight() / 2.0f;
            rabbitStartPos[i] = new PointF(centerX, centerY);
        }
    }
    private PointF getFieldCenter(Button field) {
        int[] location = new int[2];
        field.getLocationOnScreen(location);

        float X = location[0]+field.getWidth() / 2.0f;
        float Y = location[1]+field.getHeight() / 2.0f;

        return new PointF(X, Y);
    }
    private void moveRabbitOnBoard(ImageView rabbit, PointF centerField, long duration) {
        Log.d("Game", "Moving rabbit to: " + centerField.toString());
        int[] location = new int[2];
        rabbit.getLocationOnScreen(location);
        float startX = location[0];
        float startY = location[1];

        rabbit.setPivotX(0.5f * rabbit.getWidth());
        rabbit.setPivotY(1.7f * rabbit.getHeight());
        ObjectAnimator animX = ObjectAnimator.ofFloat(
                rabbit, "x", startX, centerField.x- rabbit.getPivotX());
        ObjectAnimator animY = ObjectAnimator.ofFloat(
                rabbit, "y", startY, centerField.y - rabbit.getPivotY());

//        ObjectAnimator X = ObjectAnimator.ofFloat(
//                rabbit, "translationX", centerField.x - location[0] -rabbit.getPivotX());
//        ObjectAnimator Y = ObjectAnimator.ofFloat(
//                rabbit, "translationY", centerField.y - location[1] - rabbit.getPivotY());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        animatorSet.setDuration(duration);
        animatorSet.start();
        int[] location2 = new int[2];
        rabbit.getLocationOnScreen(location);
        float startXx = location[0];
        float startYy = location[1];
        Log.d("Game", "Moved rabbit to: " + startXx + " " + startYy);

    }
    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }



}

