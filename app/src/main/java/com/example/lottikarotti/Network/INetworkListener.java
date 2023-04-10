/**

 This class represents an interface for all listeners of the socket.io client.
 It initializes and handles the network events for the client.
 */
package com.example.lottikarotti.Network;

import static com.example.lottikarotti.Network.NetworkCodes.ErrorType.*;
import static com.example.lottikarotti.Network.NetworkCodes.EmitType.*;
import static com.example.lottikarotti.Network.NetworkCodes.UniqueListenType.*;

import io.socket.client.Socket;

public interface INetworkListener {
// Error codes
public static final int UNKNOWN_ERROR_CODE = UNKNOWN_ERROR.getErrorCode();
public static final int AUTHENTICATION_ERROR_CODE = AUTHENTICATION_ERROR.getErrorCode();
public static final int NONAME_ERROR_CODE = NONAME_ERROR.getErrorCode();
public static final int LOBBY_ERROR_CODE = LOBBY_ERROR.getErrorCode();
public static final int LOBBY_CREATION_ERROR_CODE = LOBBY_CREATION_ERROR.getErrorCode();
public static final int LOBBY_JOIN_ERROR_CODE = LOBBY_JOIN_ERRROR.getErrorCode();
public static final int BAD_MOVE_ERROR_CODE = BAD_MOVE_ERROR.getErrorCode();

// Emit codes
public static final String PING_CODE = PING.getEmitCode();
public static final String REGISTER_CODE = REGISTER.getEmitCode();
public static final String GET_PLAYERS_CODE = GET_PLAYERS.getEmitCode();
public static final String PLAY_ONLINE_CODE = PLAY_ONLINE.getEmitCode();
public static final String CREATE_LOBBY_CODE = CREATE_LOBBY.getEmitCode();

// Listen codes
public static final String ERROR_CODE = ERROR.getListenCode();

/**
 * Initializes the socket instance and sets up the event listeners for the client.
 */
        default void listen(Socket instance) {
                instance.on(ERROR_CODE, code -> {
                        int errcode = (int) code[0];
                        if (errcode == UNKNOWN_ERROR_CODE) {
                                handleUnknownError();
                        } else if (errcode == AUTHENTICATION_ERROR_CODE) {
                                handleAuthenticationError();
                        } else if (errcode == NONAME_ERROR_CODE) {
                                handleNoNameError();
                        } else if (errcode == LOBBY_ERROR_CODE) {
                                handleLobbyError();
                        } else if (errcode == LOBBY_CREATION_ERROR_CODE) {
                                handleLobbyCreationError();
                        } else if (errcode == LOBBY_JOIN_ERROR_CODE) {
                                handleLobbyJoinError();
                        } else if (errcode == BAD_MOVE_ERROR_CODE) {
                                handleBadMoveError();
                        }
                });

                // Set up event listeners for emits
                instance.on(PING_CODE, args -> {
                        if (args[0].equals(1)) {
                                handlePingPong();
                        }
                });

                instance.on(REGISTER_CODE, args -> {
                        if (args[0].equals(1)) {
                                handleRegister();
                        }
                });

                instance.on(GET_PLAYERS_CODE, args -> {
                        if (args[0].equals(1)) {
                                handleGetPlayers();
                        }
                });

                instance.on(PLAY_ONLINE_CODE, args -> {
                        if (args[0].equals(1)) {
                                handlePlayOnline();
                        }
                });

                instance.on(CREATE_LOBBY_CODE, args -> {
                        if (args[0].equals(1)) {
                                handleCreateLobby();
                        }
                });
        }

        // IMPL
        void handleUnknownError();
        void handleAuthenticationError();
        void handleNoNameError();
        void handleLobbyError();
        void handleLobbyCreationError();
        void handlePingPong();
        void handleRegister();
        void handleGetPlayers();
        void handlePlayOnline();
        void handleCreateLobby();
        void handleLobbyJoinError();
        void handleBadMoveError();
}
