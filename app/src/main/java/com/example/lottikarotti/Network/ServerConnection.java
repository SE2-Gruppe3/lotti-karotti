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
    private final Activity activity;

    private static synchronized void setSocket(){
        try{
            socket = IO.socket("http://192.168.178.22:3000/");
        } catch (URISyntaxException e){
            throw new RuntimeException("Failed to create socket!", e);
        }
    }

    public static synchronized Socket getSocket(){
        if (socket == null)
            establishConnection();

    public ServerConnection(String serverUrl, Activity activity) throws URISyntaxException {
        this(IO.socket(serverUrl), activity);
    }

    public ServerConnection(Socket socket, Activity activity) {
        this.socket = socket;
        this.activity = activity;
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

    public void checkIfConnectionIsAlive(ConnectionCallback callback){
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

    public void getNumberOfConnectedPlayers(PlayerCountCallback callback){
        socket.on("getplayers", args -> {
            int number = (Integer) args[0];
            activity.runOnUiThread(() -> callback.onPlayerCountReceived(number));
        });

        socket.emit("getplayers");
    }

    public interface PlayerCountCallback {
        void onPlayerCountReceived(int count);
    }

    public void getListOfConnectedPlayers(PlayerListCallback callback){
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