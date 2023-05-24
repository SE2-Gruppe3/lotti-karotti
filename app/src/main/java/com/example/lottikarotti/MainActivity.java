package com.example.lottikarotti;



//import static com.example.lottikarotti.Network.ServerConnection.getSocket;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
        import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;


import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;

import android.annotation.SuppressLint;
        import android.graphics.PointF;
        import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
        import android.os.Handler;
import android.os.Looper;
import android.util.DisplayMetrics;
import android.util.Log;
        import android.view.ViewGroup;

        import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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
    private List<Player> players;
    private String sid;
    final int[]rabbits={
          R.id.rabbit1,R.id.rabbit2, R.id.rabbit3, R.id.rabbit4};


    PointF[] rabbitStartPos = new PointF[8];


    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4};
    final int[] holes = {
       R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};
  
    private static int hole = -1;


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
            socket = ServerConnection.getInstance("http://10.2.0.60:3000");
            ServerConnection.connect();
            Log.d(TAG, "onCreate: Connected to server");
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        ServerConnection.registerNewPlayer("Bro2");
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


        rabbit1.setImageResource(R.drawable.fig11);
        rabbit2.setImageResource(R.drawable.fig11);
        rabbit3.setImageResource(R.drawable.fig11);
        rabbit4.setImageResource(R.drawable.fig11);

        instructions= (TextView) findViewById(R.id.textViewInstructions);

        //  Initialize PlayerList Fragment and Layout
        containerplayerList = findViewById(R.id.container_playerList);
        fragmentPlayerList = new PlayerListFragment();



        for (int field : fields) {
           ImageButton button= (ImageButton)findViewById(field);
           button.setEnabled(false);
        }


       // User user = new User("testuserl", new Rabbit(1, rabbit1.getLeft(), rabbit1.getRight()), new Rabbit(2, rabbit2.getLeft(), rabbit2.getRight()), new Rabbit(3, rabbit3.getLeft(), rabbit3.getRight()), new Rabbit(4, rabbit4.getLeft(), rabbit4.getRight()));
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

        socket.on("move", args -> {
            try {
                handleMove(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle move \n" + e.toString());
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

        socket.on("createvotingpopup", args -> {
            Log.println(Log.INFO, "Voting", "Voting started");
            try {
                createVotingPopup((String) args[0], args[1].toString(), MainActivity.this);
            }catch (Exception e){
                Log.w(TAG, "Can't start voting process \n" + e.toString());
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


        /**
         * Turn Carrot
         */
        carrotButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawButton.setEnabled(true);
                 ServerConnection.carrotSpin();
                carrotButton.setEnabled(false);
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

        ImageButton votingButton = findViewById(R.id.button_vote);

        votingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View dialogView = LayoutInflater.from(MainActivity.this).inflate(R.layout.popup_voting, null);
                builder.setTitle("Enter the username of the suspected cheater: ")
                        .setView(dialogView)
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String accusedPlayer;
                                TextView username = dialogView.findViewById(R.id.txt_VotingUsername);
                                accusedPlayer = username.getText().toString();
                                socket.emit("createvotingpopup", accusedPlayer);
                            }
                        })
                        .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                Toast.makeText(getApplicationContext(), "Voting aborted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
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
        ImageButton field = (ImageButton) findViewById(fields[steps+add]);
        field.setEnabled(true);
        int puffer = steps+add;
        int addPuff = add;
        field.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                System.out.println("Sending move to server");
                ImageButton fieldtest = (ImageButton) findViewById(fields[puffer]);
                int delay = 0;
                while(fieldtest.getDrawable() != null){
                    System.out.println("Field is taken, steps + 1");
                    ++delay;
                    fieldtest =findViewById(fields[puffer+delay]);
                }
                final int finalDelay = delay;
                if(checkForHoles(puffer+finalDelay)){
                    Log.d("Hole", "onClick: " + finalDelay);
                    ServerConnection.reset(addPuff);
                    field.setEnabled(false);
                } else {
                    Log.d("Move", "onClick: " + finalDelay);
                    ServerConnection.move(steps + finalDelay, rabbit);
                    field.setEnabled(false);
                }
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
     * Handle the shake event
     */
    private void handleShake(String socketid)  {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                animateClouds(screenWidth);
                resetClouds(cloudLX, cloudRX);
            }
        });
    }

    /**
     * Create voting popup
     */

    private void createVotingPopup(String socketid, String accusedPlayer, Activity activity) {
        Handler mainHandler = new Handler(Looper.getMainLooper());

        mainHandler.post(new Runnable() {
            @Override
            public void run() {
                if(!socketid.equals(socket.id()) && !accusedPlayer.equals("Error")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                    View dialogView = LayoutInflater.from(activity).inflate(R.layout.popup_voting2, null);
                    TextView accusedPlayerTxt = dialogView.findViewById(R.id.txt_AccusedPlayer);
                    accusedPlayerTxt.setText(accusedPlayer);
                    builder.setTitle("")
                            .setView(dialogView)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    socket.emit("vote");
                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                }
                            });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                    closeVotingIfPlayerNotVotedAfterSomeTime(dialog);
                }
                else if(!accusedPlayer.equals("Error")) Toast.makeText(MainActivity.this.getApplicationContext(), "Automatically voted yes!", Toast.LENGTH_SHORT).show();
                else Toast.makeText(MainActivity.this.getApplicationContext(), "Please enter the correct Player username!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeVotingIfPlayerNotVotedAfterSomeTime(Dialog dialog) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();
                socket.emit("getvotingresult");
            }
        }, 20000);
    }

    /**
     * Handle Carrotclick (Carrotspin)
     */
    private void handleCarrotspin(String socketId, String number) throws JsonProcessingException {
        Log.d("Carrotspin", "Carrotspin received from server");
        String fieldid = "buttonfield"+number;
        Log.d("Carrotspin", "Field: "+fieldid);
        hole = Integer.parseInt(number);
        Log.d("Carrotspin", "Hole: "+hole);
        putHolesOnBoard();
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
        isMyTurn = false;
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
    private void putHolesOnBoard() {
        runOnUiThread(()-> {
            for (int hole : holes) {
                    ImageView img = (ImageView) findViewById(hole);
                    img.setVisibility(View.GONE);
                }

                ImageView img=(ImageView)findViewById(holes[hole]);
                img.setVisibility(View.VISIBLE);
                checkForRabbit();
                carrotButton.setEnabled(false);
                renderBoard();
        });
    }

    private void checkForRabbit() {
        ImageButton puffer;
        switch (hole) {
            case -1:
                break;
            case 0:
                puffer = findViewById(fields[3]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(3);
                }
                break;
            case 1:
                puffer = findViewById(fields[5]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(5);
                }
                break;
            case 2:
                puffer = findViewById(fields[7]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(7);
                }
                break;
            case 3:
                puffer = findViewById(fields[9]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(9);
                }
                break;
            case 4:
                puffer = findViewById(fields[12]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(12);
                }
                break;
            case 5:
                puffer = findViewById(fields[17]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(17);
                }
                break;
            case 6:
                puffer = findViewById(fields[19]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(19);
                }
                break;
            case 7:
                puffer = findViewById(fields[22]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(22);
                }
                break;
            case 8:
                puffer = findViewById(fields[25]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(25);
                }
                break;
            case 9:
                puffer = findViewById(fields[27]);
                if (puffer.getDrawable() != null) {
                    puffer.setImageResource(0);
                    ServerConnection.reset(27);
                }
                break;
        }
    }

    private boolean checkForHoles(int position){
        Log.d("Rabbit", "checkForHoles: " + position);
        if( hole != -1) {
            switch (position) {
                case 3:
                    if (hole == 0) {
                        return true;
                    }
                    return false;
                case 5:
                    if (hole == 1) {
                        return true;
                    }
                    return false;
                case 7:
                    if (hole == 2) {
                        return true;
                    }
                    return false;
                case 9:
                    if (hole == 3) {
                        return true;
                    }
                    return false;
                case 12:
                    if (hole == 4) {
                        return true;
                    }
                    return false;
                case 17:
                    if (hole == 5) {
                        return true;
                    }
                    return false;
                case 19:
                    if (hole == 6) {
                        return true;
                    }
                    return false;
                case 22:
                    if (hole == 7) {
                        return true;
                    }
                    return false;
                case 25:
                    if (hole == 8) {
                        return true;
                    }
                    return false;
                case 27:
                    if (hole == 9) {
                        return true;
                    }
                    return false;
            }
        } return false;

    }


    private void animateFigure(float x, float y) {
        ImageView currentRabbit =(ImageView) findViewById(rabbits[currRabbit-1]);
        currentRabbit.animate()
                .x(x - (currentRabbit.getWidth() / 2) + 50)
                .y(y - (currentRabbit.getHeight() / 2) - 60)
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

    /**
     * Override the onSensorChanged method to detect the shake gesture
     */
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
    }

    private void animateClouds(Integer screenWidth) {
        float finalPosition = ((float)screenWidth);
        // Animate Left Cloud
        cloudL.animate()
                .translationX(finalPosition / 0.7f)
                .setDuration(1000)
                .start();
        // Animate Right Cloud
        cloudR.animate()
                .translationX(-finalPosition / 0.7f)
                .setDuration(1000)
                .start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
    @Override
    protected void onResume() {
        super.onResume();
        updateBrightness();
        sensorManager.registerListener(this, shakeSensor, SensorManager.SENSOR_DELAY_NORMAL);
    }
    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }


    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
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
}

