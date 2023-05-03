package com.example.lottikarotti.Network;

import android.app.Activity;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerConnection{
    private static Socket socket;

    private static synchronized void setSocket(){
        try{
            socket = IO.socket("http://10.2.0.141:3000/");
        } catch (URISyntaxException e){
            throw new RuntimeException("Failed to create socket!", e);
        }
    }

    public static synchronized Socket getSocket(){
        establishConnection();

        if(socket == null){
            throw new IllegalStateException("Socket has not been initialized!");
        }
        return socket;
    }

    private static synchronized void establishConnection(){
        setSocket();

        if(socket == null){
            throw new IllegalStateException("Socket has not been initialized!");
        }
        if(!socket.connected()){
            socket.connect();
        }
    }

    public static synchronized void closeConnection(){
        if(socket == null){
            throw new IllegalStateException("Socket has not been initialized!");
        }
        if(socket.connected()){
            socket.disconnect();
        }
    }

    public static synchronized void checkIfConnectionIsAlive(Socket socket, Activity activity){
        socket.on("alive", args -> {
            int number = (Integer) args[0];
            boolean alive = false;
            if (number == 1) alive = true;
            boolean finalAlive = alive;
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "Connected: " + finalAlive, Toast.LENGTH_SHORT).show();
                }
            });
        });

        socket.emit("alive");
    }

    public static synchronized void getNumberOfConnectedPlayers(Socket socket, Activity activity){
        socket.on("getplayers", args -> {
            int number = (Integer) args[0];
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "Number of connected players: " + number, Toast.LENGTH_SHORT).show();
                }
            });
        });

        socket.emit("getplayers");
    }

    public static synchronized void getListOfConnectedPlayers(Socket socket, Activity activity){
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
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity.getApplicationContext(), "Name: " + names, Toast.LENGTH_SHORT).show();
                }
            });
        });

        socket.emit("getplayerlist");
    }

    public static synchronized void registerNewPlayer(Socket socket, String name){
        socket.emit("register", name);
    }

    public static synchronized void createNewLobby(Socket socket, int lobbyCode){
        socket.emit("createlobby", lobbyCode);
    }

    public static synchronized void joinLobby(Socket socket, int lobbyCode){
        socket.emit("joinlobby", lobbyCode);
    }
}
