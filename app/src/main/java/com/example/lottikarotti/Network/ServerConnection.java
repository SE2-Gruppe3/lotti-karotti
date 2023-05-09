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
    public static void shake() {
        socket.emit("shake");
    }
    public static void fetchUnique(){
        socket.emit("fetchuniqueid");
    }
    public static void carrotSpin(){
        socket.emit("carrotspin");
    }
    public static void reset(int pos){
        socket.emit("reset", pos);
    }
}
