/**

 This class represents an interface for all listeners of the socket.io client.
 It initializes and handles the network events for the client.
 */
package com.example.lottikarotti.Network;

import static com.example.lottikarotti.Network.NetworkCodes.ErrorType.*;
import static com.example.lottikarotti.Network.NetworkCodes.EmitType.*;
import static com.example.lottikarotti.Network.NetworkCodes.UniqueListenType.*;

import io.socket.client.Socket;

public class INetworkListeners {
    Socket instance; // socket instance for the network connection
// Error codes
public static final int UNKNOWN_ERROR_CODE = UNKNOWN_ERROR.getErrorCode();
public static final int AUTHENTICATION_ERROR_CODE = AUTHENTICATION_ERROR.getErrorCode();
public static final int NONAME_ERROR_CODE = NONAME_ERROR.getErrorCode();
public static final int LOBBY_ERROR_CODE = LOBBY_ERROR.getErrorCode();
public static final int LOBBY_CREATION_ERROR_CODE = LOBBY_CREATION_ERROR.getErrorCode();

// Emit codes
public static final String PING_CODE = PING.getEmitCode();
public static final String REGISTER_CODE = REGISTER.getEmitCode();
public static final String GET_PLAYERS_CODE = GET_PLAYERS.getEmitCode();
public static final String PLAY_ONLINE_CODE = PLAY_ONLINE.getEmitCode();
public static final String CREATE_LOBBY_CODE = CREATE_LOBBY.getEmitCode();

// Listen codes
public static final String ERROR_CODE = ERROR.getListenCode();

/**
 * Constructor for INetworkListeners.
 * Initializes the socket instance and sets up the event listeners for the client.
 */
        INetworkListeners() {
        // Set up error listener
        instance.on(ERROR_CODE, code -> {
        int errcode = (int) code[0];
        if (errcode == UNKNOWN_ERROR_CODE) {
        // TODO: implement handling for unknown error
        } else if (errcode == AUTHENTICATION_ERROR_CODE) {
        // TODO: implement handling for authentication error
        } else if (errcode == NONAME_ERROR_CODE) {
        // TODO: implement handling for no name error
        } else if (errcode == LOBBY_ERROR_CODE) {
        // TODO: implement handling for lobby error
        } else if (errcode == LOBBY_CREATION_ERROR_CODE) {
        // TODO: implement handling for lobby creation error
        }
        });

        // Set up event listeners for emits
        instance.on(PING_CODE, args -> {
        if (args[0].equals(1)) {
        // TODO: implement handling for ping
        }
        });

        instance.on(REGISTER_CODE, args -> {
        if (args[0].equals(1)) {
        // TODO: implement handling for register
        }
        });

        instance.on(GET_PLAYERS_CODE, args -> {
        if (args[0].equals(1)) {
        // TODO: implement handling for getting players
        }
        });

        instance.on(PLAY_ONLINE_CODE, args -> {
        if (args[0].equals(1)) {
        // TODO: implement handling for playing online
        }
        });

        instance.on(CREATE_LOBBY_CODE, args -> {
        if (args[0].equals(1)) {
        // TODO: implement handling for creating lobby
        }
        });
        }
}
