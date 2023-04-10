package com.example.lottikarotti.Network;

/**
 * This class defines types that can be returned by the server (e.g. errors, emits).
 * Please use these error codes for error handling, and add an error type and code if any new errors are added on the server.
 * <p>
 * To get the error types in any class, use "NetworkError.ErrorType.*", and to get the error code of the given error, use "NetworkError.ErrorType.*.getErrorCode".
 * The "*" represents the error, and your IDE should automatically detect these.
 */
public class NetworkCodes {

    // Enum containing different error types and their corresponding error codes
    public enum ErrorType {

        // An unknown error occurred (error code: 100)
        UNKNOWN_ERROR(100),

        // An authentication error occurred (e.g. name already taken) (error code: 400)
        AUTHENTICATION_ERROR(400),

        // No name was provided for playing error (error code: 401)
        NONAME_ERROR(401),

        // Lobby error (error code: 300)
        LOBBY_ERROR(300),

        // Lobby creation error (error code: 301)
        LOBBY_CREATION_ERROR(301),
        // Lobby join error (error code: 302)
        LOBBY_JOIN_ERRROR(302);

        // The error code for this error type
        private final int errorCode;

        // Constructor that sets the error code for each error type
        ErrorType(int errorCode) {
            this.errorCode = errorCode;
        }

        // Getter method that returns the error code for each error type
        public int getErrorCode() {
            return errorCode;
        }
    }

    // Enum containing different emit types and their corresponding emit codes
    public enum EmitType {

        // Ping emit (emit code: "alive")
        PING("alive"),

        // Register emit (emit code: "register")
        REGISTER("register"),

        // Get players emit (emit code: "getplayers")
        GET_PLAYERS("getplayers"),

        // Play online emit (emit code: "playonline")
        PLAY_ONLINE("playonline"),

        // Create lobby emit (emit code: "createlobby")
        CREATE_LOBBY("createlobby");

        // The emit code for this emit type
        private final String emitCode;

        // Constructor that sets the emit code for each emit type
        EmitType(String emitCode) {
            this.emitCode = emitCode;
        }

        // Getter method that returns the emit code for each emit type
        public String getEmitCode() {
            return emitCode;
        }
    }

    // Enum containing different unique listen types and their corresponding listen codes
    public enum UniqueListenType {

        // Error listen (listen code: "error")
        ERROR("error");

        // The listen code for this listen type
        private final String listenCode;

        // Constructor that sets the listen code for each listen type
        UniqueListenType(String listenCode) {
            this.listenCode = listenCode;
        }

        // Getter method that returns the listen code for each listen type
        public String getListenCode() {
            return listenCode;
        }

    }
}
