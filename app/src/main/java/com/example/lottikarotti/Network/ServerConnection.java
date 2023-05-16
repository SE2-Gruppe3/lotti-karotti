package com.example.lottikarotti.Network;

import android.app.Activity;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerConnection {
    private static final String TAG = ServerConnection.class.getSimpleName();
    private static Socket socket;

    public static synchronized Socket getInstance(String serverUrl) throws URISyntaxException {
        if (socket == null) {
            socket = IO.socket(serverUrl);
        }
        return socket;
    }

    public static void connect() {
        if (!socket.connected()) {
            socket.connect();
        }
    }

    public static void disconnect() {
        if (socket.connected()) {
            socket.disconnect();
        }
    }

    public static void checkIfConnectionIsAlive(Activity activity, ConnectionCallback callback) {
        socket.on("alive", args -> {
            int number = (Integer) args[0];
            boolean alive = (number == 1);
            activity.runOnUiThread(() -> callback.onConnectionChecked(alive));
        });

        socket.emit("alive");
    }

    public interface ConnectionCallback {
        void onConnectionChecked(boolean isAlive);
    }

    public static void getNumberOfConnectedPlayers(Activity activity, PlayerCountCallback callback) {
        socket.on("getplayers", args -> {
            int number = (Integer) args[0];
            activity.runOnUiThread(() -> callback.onPlayerCountReceived(number));
        });

        socket.emit("getplayers");
    }

    public interface PlayerCountCallback {
        void onPlayerCountReceived(int count);
    }

    public static void getListOfConnectedPlayers(Activity activity, PlayerListCallback callback) {
        socket.on("getplayerlist", args -> {
            JSONArray playerList = (JSONArray) args[0];
            List<String> names = new ArrayList<>();
            for (int i = 0; i < playerList.length(); i++) {
                try {
                    JSONObject object = playerList.getJSONObject(i);
                    names.add(object.getString("name"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            activity.runOnUiThread(() -> callback.onPlayerListReceived(names));
        });

        socket.emit("getplayerlist");
    }

    public interface PlayerListCallback {
        void onPlayerListReceived(List<String> playerList);
    }


    public static void getHighScoreBoard(Activity activity, HighScoreBoardCallback callback){
        socket.on("gethighscore", args -> {
            JSONArray highScore = (JSONArray) args[0];
            List<String> usernames = new ArrayList<>();
            List<Integer> scores = new ArrayList<>();
            for(int i=0; i<highScore.length(); i++){
                try {
                    JSONObject object = highScore.getJSONObject(i);
                    usernames.add(object.getString("username"));
                    scores.add(object.getInt("score"));
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            activity.runOnUiThread(() -> callback.onHighScoreBoardReceived(usernames, scores));
        });

        socket.emit("gethighscore");
    }

    public interface HighScoreBoardCallback {
        void onHighScoreBoardReceived(List<String> usernames, List<Integer> scores);
    }

    public static void updateHighScoreBoard(String winnerUsername) {
        socket.on("gethighscore", args -> {
            JSONArray highScore = (JSONArray) args[0];
            JSONArray updatedHighScore = new JSONArray();

            boolean newPlayer = false;
            for(int i=0; i<highScore.length(); i++){
                try {
                    JSONObject object = highScore.getJSONObject(i);
                    if(object.getString("username").equals(winnerUsername)) {
                        int newScore = object.getInt("score") + 1;
                        object.put("score", newScore);
                    }

                    else if(!object.getString("username").contains(winnerUsername)) {
                        newPlayer = true;
                    }

                    updatedHighScore.put(object);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }

            if(newPlayer){
                JSONObject object = new JSONObject();
                try {
                    object.put("username", winnerUsername);
                    object.put("score", 1);
                    updatedHighScore.put(object);
                } catch (JSONException e) {
                    throw new RuntimeException(e);
                }
            }
            socket.emit("saveupdatedhighscore", updatedHighScore);
        });
    }

    public void drawCard(Activity activity, DrawCardCallback callback){
        socket.on("drawcard", args -> {
            int number = (Integer) args[0];
            activity.runOnUiThread(() -> callback.onCardDrawn(number));
        });

        socket.emit("drawcard");
    }

    public interface DrawCardCallback {
        void onCardDrawn(int random);
    }
    public static void registerNewPlayer(String name) {
        socket.emit("register", name);
    }

    public static void createNewLobby(String lobbyCode) {
        socket.emit("createlobby", lobbyCode);
    }

    public static void joinLobby(String lobbyCode) {
        socket.emit("joinlobby", lobbyCode);
    }

    public static void move(int steps, int rabbitNo) {
        socket.emit("move", steps, rabbitNo);
    }
    public static void cheatMove(int pos, int rabbitNo) {
        socket.emit("moveCheat", pos, rabbitNo);
    }

    public static void shake() {
        socket.emit("shake");
    }
    public static void fetchUnique(){
        socket.emit("fetchuniqueid");
    }
    public static void carrotSpin(String lobbyCode){
        socket.emit("carrotspin");
    }
    public static void cheat(String name){
        socket.emit("cheat",name);
    }
    public static void reset(int pos){
        socket.emit("reset", pos);
    }
    public static void checkHole(String lobbyCode) { socket.emit ("checkhole", lobbyCode); }
}
