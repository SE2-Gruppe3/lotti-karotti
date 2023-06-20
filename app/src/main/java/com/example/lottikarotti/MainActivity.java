package com.example.lottikarotti;


//import static com.example.lottikarotti.Network.ServerConnection.getSocket;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PointF;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.method.ScrollingMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.lottikarotti.GameLogic.PlayerMove;
import com.example.lottikarotti.Listeners.IOnDataSentListener;
import com.example.lottikarotti.Network.CheckConnectionHandler;
import com.example.lottikarotti.Network.InitializeConnectionHandler;
import com.example.lottikarotti.Network.ServerConnection;
import com.fasterxml.jackson.core.JsonProcessingException;

import org.apache.commons.lang3.ArrayUtils;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import io.socket.client.Socket;

public class MainActivity extends AppCompatActivity implements IOnDataSentListener, SensorEventListener, MutatorDialog.MutatorDialogListener, GameEndDialog.GameEndDialogListener {

    private String lobbyId;
    private String username;
    private WaitingDialog waitDialog;
    private String info;
    private Button carrotButton;
    private ImageButton settingsButton;
    public Button drawButton;
    private Button startTurn;
    private Button endTurn;
    private TextView turnInstruction;
    private ImageView cardView;
    public ImageView rabbit1;
    public ImageView rabbit2;
    public ImageView rabbit3;
    public ImageView rabbit4;

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
    private String[] optionsArray;
    private String accusedPlayer;
    private static final int SHAKE_THRESHOLD = 1000;

    //Variables for the Clouds
    private Button cloudButton;
    private int screenWidth;
    private int screenHeight;
    private ImageView cloudL;
    private ImageView cloudR;
    private int cloudLX;
    private int cloudRX;
    public boolean isMyTurn;
    private boolean isCheating;
    private int result;
    public boolean gameStarted;
    private int hole;
    public List<Player> players;
    private ImageButton rotateTurnButton;

    final int[] rabbits = {
            R.id.rabbit1, R.id.rabbit2, R.id.rabbit3, R.id.rabbit4};

    private static final String URI = "http://192.168.68.52:3000";


    PointF[] rabbitStartPos = new PointF[8];
    final int[] cards = {
            R.drawable.card1, R.drawable.card2, R.drawable.card3,
            R.drawable.card4, R.drawable.card5 };
    final int[] holes = { R.id.hole0,
            R.id.hole3, R.id.hole5,R.id.hole7,R.id.hole9,R.id.hole12,R.id.hole17,R.id.hole19,
            R.id.hole22,R.id.hole25,R.id.hole27};
    private static int currHole = -1;

    final int[] fields = { R.id.buttonField1,
            R.id.buttonField1, R.id.buttonField2,R.id.buttonField3,R.id.buttonField4,R.id.buttonField5,R.id.buttonField6,R.id.buttonField7,
            R.id.buttonField8,R.id.buttonField9,R.id.buttonField10, R.id.buttonField11, R.id.buttonField12, R.id.buttonField13, R.id.buttonField14,
            R.id.buttonField15, R.id.buttonField16, R.id.buttonField17, R.id.buttonField18, R.id.buttonField19, R.id.buttonField20,
            R.id.buttonField21, R.id.buttonField22, R.id.buttonField23, R.id.buttonField24,R.id.buttonField25,R.id.buttonField26,R.id.buttonField27, R.id.buttonField28,R.id.buttonField29};

    public Socket socket;
    private String activemutator;
    public void MainActivity() {
        onCreate(null);
    }
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initializeServerConnection();

        initializeIntent();
        initializeSensors();
        initializeDrawables();
        initializeFragments();
        initializeLogic();
        initializeButtons();
        initializeServerListeners();
        initializeClouds();
        initializeButtonLogic();
        initializeTurnLogic();
        checkServerConnected();
    }

    /**
     * Check server connectivity
     * If the server is not connected the activitiy gets finished!
     */
    private void checkServerConnected(){
        // Test if server has connection, finish activity if not
        if (CheckConnectionHandler.check(socket) == false) finish();
    }
    /**
     * This method is called when the activity is created.
     */
    private void initializeServerConnection() {
        players = new ArrayList<>();
        socket = InitializeConnectionHandler.initialize(socket, URI, this);
    }

    /**
     * This method is called when the activity is created.
     */
    private void initializeIntent() {
        Intent intent = getIntent();
        lobbyId = intent.getStringExtra("lobbyId");

         username = intent.getStringExtra("username");

        info = intent.getStringExtra("info");

        TextView lobbyID = findViewById(R.id.lobbyID);
        lobbyID.setText("Lobby ID: " + lobbyId);

        ServerConnection.registerNewPlayer(username);
        ServerConnection.fetchUnique();

        if (info.equals("start")) {
            ServerConnection.createNewLobby(lobbyId);
            showMutatorDialong();
            Log.d(TAG, "onCreate: Created new lobby" + lobbyId);
        } else {
            ServerConnection.joinLobby(lobbyId);
            waitDialog = new WaitingDialog();
            waitDialog.show(getSupportFragmentManager(), "WaitingDialog");
            Log.d(TAG, "Joined lobby" + lobbyId);
        }
            }

    /**
     * This method is called when the activity is created.
     */
    public void initializeSensors() {

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        shakeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeDrawables() {
        rabbit1 = (ImageView) findViewById(R.id.rabbit1);
        rabbit2 = (ImageView) findViewById(R.id.rabbit2);
        rabbit3 = (ImageView) findViewById(R.id.rabbit3);
        rabbit4 = (ImageView) findViewById(R.id.rabbit4);

        rabbit1.setImageResource(R.drawable.fig11);
        rabbit2.setImageResource(R.drawable.fig11);
        rabbit3.setImageResource(R.drawable.fig11);
        rabbit4.setImageResource(R.drawable.fig11);

        instructions = findViewById(R.id.textViewInstructions);
    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeFragments() {
        //  Initialize PlayerList Fragment and Layout
        containerplayerList = findViewById(R.id.container_playerList);
        fragmentPlayerList = new PlayerListFragment();
    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeLogic() {
        //Disale all fields at the start of the game
        for (int field : fields) {
            ImageButton button = findViewById(field);
            button.setEnabled(false);
        }
    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeButtons() {
        rotateTurnButton = findViewById(R.id.button_rotateturn);
        carrotButton = findViewById(R.id.carrotButton);
        cardView = (ImageView) findViewById(R.id.imageViewCard);
        settingsButton = (ImageButton) findViewById(R.id.settings);
        drawButton = (Button) findViewById(R.id.drawCard);
        turnInstruction = (TextView)findViewById(R.id.turnInstruction);
        turnInstruction.setText("Host is starting game ! ");
        drawButton.setEnabled(false);
        carrotButton.setEnabled(false);

        instructions.setText(" Please choose a rabbit to play !");
        instructions.setMovementMethod(new ScrollingMovementMethod());
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
    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeServerListeners() {
        //  Initialize Server Listener "move", listen the move to the server
        socket.on("move", args -> {
            try {
                handleMove(args[0].toString());
            } catch (Exception e) {
                Log.w(TAG, "Can't handle move \n" + e.toString());
            }
        });


        // Initialize Server Listener "moveCheat", listen the move to the server in case of cheating
        socket.on("moveCheat", args -> {
            try {
                handleMove(args[0].toString());
            } catch (Exception e) {
                Log.w(TAG, "Can't handle move cheat \n" + e.toString());
            }
        });

        //  Initialize Server Listener "move", listen the shake event to the server
        socket.on("shake", args -> {
            Log.println(Log.INFO, "Shake", "Shake received");
            try {
                handleShake(args[0].toString());
            } catch (Exception e) {
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

        socket.on("checkifplayercheated", args -> {
            Log.println(Log.INFO, "Cheater", "Getting cheater");
            try {
                getCheater((String) args[0], (int) args[1]);
            }catch (Exception e){
                Log.w(TAG, "Can't start voting process \n" + e.toString());
            }
        });

        // Initialize Server Listener "carrotspin", listen the carrotspin event to the server, in case the carrot has been spun
        socket.on("carrotspin", args -> {
            Log.println(Log.INFO, "Carrot", "carrotspin received");
            try {
                handleCarrotspin(args[0].toString());
            } catch (Exception e) {
                Log.w(TAG, "Can't handle carrotspin \n" + e.toString());
            }
        });

        // Initialize Server Listener "turn", listen the turn event to the server
        socket.on("turn", id -> {
            Log.println(Log.INFO, TAG, "Turn received" + id[0].toString() + "<-gerver - l0cal->" + socket.id());

            if (id[0].toString().equals(socket.id().toString())) setMyTurn(true);

        });
        // Initialize Server Listener "turn", listen the turn event to the server
        socket.on("isTurnOf", us -> {
            Log.println(Log.INFO, TAG, "Turn of received"   );
            handleIsTurnOf(us[0].toString());
        });
        // Initialize Server Listener "turn", listen to the turn event from the server, see who's turn it is
        socket.on("error", code -> {
            Log.println(Log.INFO, TAG, "Server indicates error! Code: " + code[0].toString());
            runOnUiThread(() -> {
                Toast.makeText(getApplicationContext(), "Server indicates error! Code: " + code[0].toString(), Toast.LENGTH_SHORT).show();
                if (code[0].toString().equals("302") || code[0].toString().equals("303")) finish();
            });

        });

        // Initialize Server Listener "startgame", listen to the startgame event from the server, see if the game has started
        socket.on("startgame", args -> {
            Log.println(Log.INFO, TAG, "Game start recieved");
            gameStarted = true;
        });

        socket.on("mutatorSelected", args -> {
            Log.println(Log.INFO, TAG, "Mutator selected recieved");
            if (!(info.equals("start")) && waitDialog != null) {
                Log.d(TAG, "onCreate: Dismissing dialog");
                runOnUiThread(() -> {
                    waitDialog.dismiss();
                });
            }
        });

        socket.on("spicycarrotspin", args -> {
            Log.println(Log.INFO, "Carrot", "spicycarrotspin received");
            try {
                handleSpicyCarrotspin(args[0].toString(), args[1].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle spicycarrotspin \n" + e.toString());
            }
        });

        socket.on("winning", args -> {
            Log.println(Log.INFO, "WIN", "winning received");
            try {
                handleWinning(args[0].toString());
            }catch (Exception e){
                Log.w(TAG, "Can't handle winning \n" + e.toString());
            }
        });

        socket.on("getMutator", args -> {
            Log.d(TAG, "Mutator saved locally: " + args[0].toString());
            activemutator = args[0].toString();
            if (activemutator.equals("specialCard")) maxCase = 5;
        });

    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeClouds() {
        DisplayMetrics displayMetrics = new DisplayMetrics(); 
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

        // Programmatically set clouds (dynamically)
        ViewGroup.LayoutParams cloudLeftParam = cloudL.getLayoutParams();
        ViewGroup.LayoutParams cloudRightParam = cloudR.getLayoutParams();

        cloudLeftParam.width = screenWidth * 2;
        cloudLeftParam.height = screenHeight / 2;
        cloudL.setLayoutParams(cloudLeftParam);

        cloudRightParam.width = screenWidth * 2;
        cloudRightParam.height = screenHeight / 2;
        cloudR.setLayoutParams(cloudRightParam);
    }
    private void resetRabbitBorder(int rabbit){
        for (int i = 0; i < rabbits.length; i++) {
            if(i != rabbit){
                ImageView otherRabbit = findViewById(rabbits[i]);
                otherRabbit.setBackgroundResource(R.color.white);

            }
        }

    }

    /**
     * This method is called when the activity is created.
     */
    public void initializeButtonLogic() {

        if (info.equals("start")){
            Log.d(TAG, "initializeButtonLogic: Rotate turn button visible for host");
            rotateTurnButton.setVisibility(View.VISIBLE);
            rotateTurnButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    runOnUiThread(() -> {
                        socket.emit("hostTurn", 1);
                    });
                }
            });
        }
        rabbit1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(0);
                rabbit1.setBackgroundResource(R.drawable.border_fragment);
                resetRabbitBorder(0);
                Toast.makeText(getApplicationContext(), "Rabbit 1", Toast.LENGTH_SHORT).show();

            }
        });
        rabbit2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(1);
                rabbit2.setBackgroundResource(R.drawable.border_fragment);
                resetRabbitBorder(1);
                Toast.makeText(getApplicationContext(), "Rabbit 2", Toast.LENGTH_SHORT).show();


            }
        });
        rabbit3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(2);
                rabbit3.setBackgroundResource(R.drawable.border_fragment);
                resetRabbitBorder(2);
                Toast.makeText(getApplicationContext(), "Rabbit 3", Toast.LENGTH_SHORT).show();

            }
        });
        rabbit4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectRabbit(3);
                rabbit4.setBackgroundResource(R.drawable.border_fragment);
                resetRabbitBorder(3);
                Toast.makeText(getApplicationContext(), "Rabbit 4", Toast.LENGTH_SHORT).show();

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
                ServerConnection.carrotSpin();

                drawButton.setEnabled(false);
                carrotButton.setEnabled(false);

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
                ServerConnection.getListOfConnectedPlayers(MainActivity.this, new ServerConnection.PlayerListCallback() {
                    @Override
                    public void onPlayerListReceived(List<String> playerList) {
                        optionsArray = new String[playerList.size()];
                        optionsArray = playerList.toArray(optionsArray);

                        Spinner spinner = dialogView.findViewById(R.id.txt_VotingUsername);

                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, optionsArray);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                accusedPlayer = parent.getItemAtPosition(position).toString();
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> adapterView) {
                                //
                            }
                        });
                    }
                });

                builder.setTitle("Select the username of the suspected cheater: ")
                        .setView(dialogView)
                        .setPositiveButton("Start", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
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

    /**
     * This method is called when the activity is created.
     */
    public void initializeTurnLogic() {
        setMyTurn(false);
        gameStarted = false;
    }

    private void setColorForRabbits() {
        Log.d("Rabbit", "setColorForRabbits: " + players.size());
        Map<String, Integer> colorDrawableMap = new HashMap<>();
        colorDrawableMap.put("white", R.drawable.fig11);
        colorDrawableMap.put("red", R.drawable.fig88);
        colorDrawableMap.put("pink", R.drawable.fig22);
        colorDrawableMap.put("green", R.drawable.fig77);

        for (Player player : players) {
            if (player.getSid().equals(socket.id())) {
                Integer drawableId = colorDrawableMap.get(player.getColor());
                if (drawableId != null) {
                    rabbit1.setImageResource(drawableId);
                    rabbit2.setImageResource(drawableId);
                    rabbit3.setImageResource(drawableId);
                    rabbit4.setImageResource(drawableId);
                    Log.d("Rabbit", "setColorForRabbits: " + player.getColor());
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
        if(!isMyTurn) {
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
    private void playerMove(int steps, int rabbit) {
        if (!isMyTurn) return;

        if (!gameStarted) socket.emit("startgame", 1);

        setMyTurn(false);
        drawButton.setEnabled(false);

        System.out.println(steps + " steps with rabbit " + rabbit);

        int add = players.stream()
                .filter(player -> socket.id().equals(player.getSid()))
                .map(player -> player.getRabbits().get(rabbit).getPosition())
                .findFirst()
                .orElse(0);

        if((steps+add) <= 29 && (steps+add) >= 0) {
            ImageButton field = findViewById(fields[steps + add]);
            field.setBackgroundResource(R.drawable.border_field);
            field.setEnabled(true);

            field.setOnClickListener(view -> {
                System.out.println("Sending move to server");
                //ImageButton fieldtest = findViewById(fields[steps + add]);
                //int delay = 0;
                ServerConnection.move(steps, rabbit);
                field.setEnabled(false);
            });
        } else {
            System.out.println("Sending move to server");
            ServerConnection.move(0, rabbit);
        }
    }


    /**
     * Movement handler
     * Annotate JSON Values to @Class Player and @Class Rabbit
     */
    private void handleMove(String json) throws JsonProcessingException {
        players = PlayerMove.handleMoveFromServer(json);
        renderBoard();
    }

    private void handleIsTurnOf(String username){

        turnInstruction.setText("Turn of: "+username);
    }
    /**
     * Handle the shake event
     */
    private void handleShake(String socketid) {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post(() -> {
            if (!socketid.equals(socket.id())) {
                animateClouds(screenWidth);
                resetClouds(cloudLX, cloudRX);
            } else {
                instructions.setText("You are now able to cheat, others can't see you!!");
                instructions.setTextColor(Color.parseColor("#FFA500"));


                for (int i = 1; i < fields.length; i++) {
                    ImageButton field = findViewById(fields[i]);
                    field.setEnabled(true);

                    field.setOnClickListener(view -> {
                        int position = ArrayUtils.indexOf(fields, field.getId());
                        System.out.println("Sending move to server");
                        ImageButton fieldtest = findViewById(fields[position]);
                        int delay = 0;
                        while (fieldtest.getDrawable() != null) {
                            System.out.println("Field is taken, steps + 1");
                            ++delay;
                            fieldtest = findViewById(fields[delay + position]);
                        }
                        final int finalDelay = delay + position;
                        ServerConnection.cheatMove(finalDelay, currRabbit);
                        ServerConnection.cheat(socketid);
                        field.setEnabled(false);
                    });
                }

                Toast.makeText(MainActivity.this, "Please choose the field you want to move", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Handle the winning
     */
    private void handleWinning(String winner) {
        if(info.equals("start")) {
            showGameEndDialog(winner, true);
        } else if (!(info.equals("start"))) {
            showGameEndDialog(winner, false);
        }
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
                    closeVotingAfterSomeTime(dialog, accusedPlayer);
                }
                else if(!accusedPlayer.equals("Error")) Toast.makeText(MainActivity.this.getApplicationContext(), "Automatically voted yes!", Toast.LENGTH_SHORT).show();
                else if(socketid.equals(socket.id())) Toast.makeText(MainActivity.this.getApplicationContext(), "Please enter the correct Player username!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void closeVotingAfterSomeTime(Dialog dialog, String player) {
        Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                dialog.dismiss();

                socket.on("getvotingresult", args -> {
                    result = (int) args[0];

                    socket.emit("checkifplayercheated", player);
                });

                socket.emit("getvotingresult");
            }
        }, 20000);
    }

    /**
     * Handle the Cheating
     */
    private void getCheater(String cheaterResponse, int percentage){
        Handler mainHandler = new Handler(Looper.getMainLooper());
        mainHandler.post( new Runnable() {

            @Override
            public void run() {
                isCheating = Boolean.parseBoolean(cheaterResponse);
                result = percentage;

                if(result >= 50 && isCheating){
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Remove rabbits from cheater!", Toast.LENGTH_SHORT).show();
                        ServerConnection.punish(accusedPlayer);
                    });
                }
                else if(isCheating) {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Not enough yes votes!", Toast.LENGTH_SHORT).show();
                    });
                }
                else {
                    runOnUiThread(() -> {
                        Toast.makeText(getApplicationContext(), "Player did not cheat! Removing your rabbits! ", Toast.LENGTH_SHORT).show();
                        ServerConnection.punish(accusedPlayer);
                    });
                }
            }
        });
    }

    /**
     * Handle Carrotclick (Carrotspin)
     */
    private void handleCarrotspin(String holeIndex) throws JsonProcessingException {
        try {
            putHolesOnBoard(Integer.parseInt(holeIndex), -1);
            ServerConnection.move(0,0);
        }catch (Exception ex){
            //TODO: handle exception

        }

    }

    private void handleSpicyCarrotspin(String holeOneIndex, String holeTwoIndex) {
        try {
            putHolesOnBoard(Integer.parseInt(holeOneIndex), Integer.parseInt(holeTwoIndex));
            ServerConnection.move(0,0);
        }catch (Exception ex){
            //TODO: handle exception
        }
    }


    /**
     * Renders the board to a pulp will get updated, shitcode is temporary
     */
    private void renderBoard() {
        for (int x : fields) {
            runOnUiThread(() -> {
                ImageButton btn = findViewById(x);
                btn.setBackgroundColor(0);
                btn.setImageResource(0);
                btn.setEnabled(false);
            });
        }
        Log.d("Rabbit", "Renderboard: " + players.size());
        for (Player gayer : players) {
            String color = gayer.getColor();
            List<Rabbit> tempRabbits = gayer.getRabbits();
            Log.d("Rabbit", "Renderboard: Rabbit color: " + color);
            for (Rabbit rabbit : tempRabbits) {
                if (rabbit.getPosition() > 0) {
                    Log.d("Rabbit", "Renderboard.Rabbit: " + rabbit.getName());
                    runOnUiThread(() -> {
                        System.out.println("Renderboard.Drawing rabbit on field " + rabbit.getPosition());
                        ImageButton rabbitbtn = findViewById(fields[rabbit.getPosition()]);
                        rabbitbtn.setOnClickListener(
                                new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Toast.makeText(getApplicationContext(), ""+rabbit.getName(), Toast.LENGTH_SHORT).show();

                                    }
                                });
                        setColorForRabbitsRender(rabbitbtn, color);
                        rabbitbtn.setEnabled(true);
                    });
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
    private void putHolesOnBoard(int hole1, int hole2) {
        runOnUiThread(()-> {
            for (int h : holes) {
                ImageView img = (ImageView) findViewById(h);
                img.setVisibility(View.INVISIBLE);
            }
            if (hole2 != -1) {
                ImageView img = (ImageView) findViewById(holes[hole2]);
                img.setVisibility(View.VISIBLE);
                checkForRabbit(hole2);
            }
            ImageView img=(ImageView)findViewById(holes[hole1]);
            img.setVisibility(View.VISIBLE);
            checkForRabbit(hole1);
            carrotButton.setEnabled(false);
            playerMove(0, 0);
            //renderBoard();
        });
    }

    private void checkForRabbit(int hole) {
        int[] fieldPositions = {3, 5, 7, 9, 12, 17, 19, 22, 25, 27};
        int fieldIndex = hole - 1;
        if (fieldIndex >= 0 && fieldIndex < fieldPositions.length) {
            ImageButton puffer = findViewById(fields[fieldPositions[fieldIndex]]);
            if (puffer.getDrawable() != null) {
                puffer.setImageResource(0);
                ServerConnection.reset(fieldPositions[fieldIndex]);
            }
        }
    }


    private void checkForHoles(int currPos, int currHole, int desiredPos, int rabbit) {
        Log.d("Rabbit", "checkForHoles: " + currPos);
        if (desiredPos == 3 || desiredPos == 5 || desiredPos == 7 || desiredPos == 9 || desiredPos == 12 || desiredPos == 17 || desiredPos == 19 || desiredPos == 22 || desiredPos == 25 || desiredPos == 27) {
            if (currHole == desiredPos - currPos) {
                ServerConnection.reset(currPos);
            } else {
                Log.d("Move", "onClick: " + desiredPos);
                ServerConnection.move(desiredPos - currPos, rabbit);
            }
        } else {
            ServerConnection.move(desiredPos - currPos, rabbit);
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
            currRabbit++;
            instructions.setText("Instructions: You are playing with Rabbit "+currRabbit);
            drawButton.setEnabled(true);

        }

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
        if(myTurn == true){
            ServerConnection.isTurnOf(username);
            instructions.setTextColor(Color.BLACK);
            instructions.setText(" Your turn, please choose a rabbit");
            resetRabbitBorder(4);
             }
        else{
            instructions.setTextColor(Color.BLACK);
            instructions.setText(" Not your turn, please wait");

        }

        isMyTurn = myTurn;
        togglePlayerRabbits();
        Log.d("Game", "setMyTurn: " + isMyTurn);
    }
    private void togglePlayerRabbits() {
        Log.d("Game", "togglePlayerRabbits: " + isMyTurn);
        PlayerMove.rabbitToggle(this, isMyTurn);
    }
    private void togglePlayerRabbits(boolean activate) {
        Log.d("Game", "togglePlayerRabbits: " + isMyTurn);
        PlayerMove.rabbitToggle(this, activate);
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

        rabbit.setPivotX(0.5f * rabbit.getWidth());
        rabbit.setPivotY(1.7f * rabbit.getHeight());

        ObjectAnimator animX = ObjectAnimator.ofFloat(rabbit, "x", rabbit.getX(), centerField.x - rabbit.getPivotX());
        ObjectAnimator animY = ObjectAnimator.ofFloat(rabbit, "y", rabbit.getY(), centerField.y - rabbit.getPivotY());

        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(animX, animY);
        animatorSet.setDuration(duration);
        animatorSet.start();

        float startXx = rabbit.getX();
        float startYy = rabbit.getY();
        Log.d("Game", "Moved rabbit to: " + startXx + " " + startYy);
    }

    private void updateBrightness() {
        SharedPreferences sharedBrightness = getSharedPreferences("settings", MODE_PRIVATE);
        int brightness = sharedBrightness.getInt("brightness", 100);
        WindowManager.LayoutParams layoutPar = getWindow().getAttributes();
        layoutPar.screenBrightness = brightness / 255f;
        getWindow().setAttributes(layoutPar);
    }

    private void showMutatorDialong() {
        MutatorDialog mutatorDialog = new MutatorDialog(this,lobbyId);
       mutatorDialog.show(getSupportFragmentManager(), "MutatorDialog");



    }

    @Override
    public void onGameModeSelected(int mutator) {
        if(!(info.equals("start"))) {
            waitDialog.dismiss();
        }
        switch (mutator) {
            case 0:
                ServerConnection.setMutator("classic");
                Toast.makeText(MainActivity.this, "Classic mode: No Mutator selected!", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                ServerConnection.setMutator("spicyCarrot");
                Toast.makeText(MainActivity.this, "Spicy Carrot selected!", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                ServerConnection.setMutator("specialCard");
                Toast.makeText(MainActivity.this, "Special Card selected!", Toast.LENGTH_SHORT).show();
                break;
        }

    }
    private void showGameEndDialog(String winner, boolean isHost) {
        GameEndDialog gameEndDialog = new GameEndDialog(this, winner, isHost);
        gameEndDialog.show(getSupportFragmentManager(), "GameEndDialog");
    }

    @Override
    public void onRestartGame() {
        finish();
    }

    @Override
    public void onExitGame(boolean isHost) {
        if(!isHost){
            ServerConnection.disconnectServer();
            finish();
        } else if (isHost){
            ServerConnection.disconnectServer();
            finish();
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            ServerConnection.getInstance("xxx").disconnect();
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
    private int maxCase = 4;
    /**
     * Button Listeners
     */
    public void drawButtonListener(View view){
        Random rand = new Random();
        int random = rand.nextInt(maxCase);
        cardView.setImageResource(cards[random]);
        instructions.setTextColor(Color.BLACK);

        switch (random) {
            case 0:
                drawButton.setEnabled(false);
                carrotButton.setEnabled(false);
                instructions.setText("Instructions: Move three fields with your rabbit on the game board");
                    playerMove(3, currRabbit);
                    break;
                case 1:
                    togglePlayerRabbits(false);
                    drawButton.setEnabled(false);
                    instructions.setText("Instructions: Click the carrot on the game board");
                    carrotButton.setEnabled(true);
                    break;
                case 2:
                    drawButton.setEnabled(false);
                    instructions.setText("Instructions: Move one field with your rabbit on the game board");
                    carrotButton.setEnabled(false);
                    playerMove(1, currRabbit);
                    break;
                case 3:
                    drawButton.setEnabled(false);
                    carrotButton.setEnabled(false);
                    instructions.setText("Instructions: Move two fields with your rabbit on the game board");
                    playerMove(2, currRabbit);
                    break;
                case 4:
                    drawButton.setEnabled(false);
                    instructions.setText("Instructions: Do you move? Are you getting set back or maybe racing in front?!");
                    carrotButton.setEnabled(false);
                    playerMove(rand.nextInt(10)-3, currRabbit);
                    break;
            }
    }
}

