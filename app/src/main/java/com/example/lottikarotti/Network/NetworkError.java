package com.example.lottikarotti.Network;

/**
 * This class defines error types that can be returned by the server
 * Please use these error Codes for error handling, and please add an error type + code if you add any new error on the server
 * <p>
 * in any class just use "NetworkError.ErrorType.*" to get the error types, use "NetworkError.ErrorType.*.getErrorCode" to get the ErrorCode of the given error
 * the "*" represents the Error, your IDE should automatically detect these
 */

public class NetworkError {
    // Enum containing different error types and their corresponding error codes

    public enum ErrorType {
        // An unknown error occurred (error code: 100)
        UNKNOWN_ERROR(100),
        // An authentication error occurred (e.g name already taken) (error code: 400)
        AUTHENTICATION_ERROR(400);

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
}
