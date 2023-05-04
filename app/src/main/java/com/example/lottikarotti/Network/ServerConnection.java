package com.example.lottikarotti.Network;

import android.app.Activity;
import android.media.browse.MediaBrowser;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerConnection{
    private final Socket socket;

    public ServerConnection(String serverUrl) throws URISyntaxException {
        this.socket = IO.socket(serverUrl);
    }

    public Socket getSocket(){
        return socket;
    }

    public void connect(){
        if(!socket.connected()) {
            socket.connect();
        }
    }

    public void disconnect() {
        if(socket.connected()) {
            socket.disconnect();
        }
    }

    public void checkIfConnectionIsAlive(Activity activity, ConnectionCallback callback){
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

    public void getNumberOfConnectedPlayers(Activity activity, PlayerCountCallback callback){
        socket.on("getplayers", args -> {
            int number = (Integer) args[0];
            activity.runOnUiThread(() -> callback.onPlayerCountReceived(number));
        });

        socket.emit("getplayers");
    }

    public interface PlayerCountCallback {
        void onPlayerCountReceived(int count);
    }

    public void getListOfConnectedPlayers(Activity activity, PlayerListCallback callback){
        socket.on("getplayerlist", args -> {
            JSONArray playerList = (JSONArray) args[0];
            List<String> names = new ArrayList<>();
            for(int i=0; i<playerList.length(); i++){
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

    public void registerNewPlayer(String name){
        socket.emit("register", name);
    }

    public void createNewLobby(String lobbyCode){
        socket.emit("createlobby", lobbyCode);
    }

    public void joinLobby(String lobbyCode){
        socket.emit("joinlobby", lobbyCode);
    }
}
