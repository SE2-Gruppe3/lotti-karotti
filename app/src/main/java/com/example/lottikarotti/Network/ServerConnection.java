package com.example.lottikarotti.Network;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class ServerConnection{
    private static Socket socket;

    public static synchronized void setSocket(){
        try{
            socket = IO.socket("http://10.2.0.141:3000/");
        } catch (URISyntaxException e){
            throw new RuntimeException("Failed to create socket!", e);
        }
    }

    public static synchronized Socket getSocket(){
        if(socket == null){
            throw new IllegalStateException("Socket has not been initialized!");
        }
        return socket;
    }

    public static synchronized void establishConnection(){
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
}
