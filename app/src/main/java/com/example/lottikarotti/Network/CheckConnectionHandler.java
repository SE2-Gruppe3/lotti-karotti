package com.example.lottikarotti.Network;

import io.socket.client.Socket;

public class CheckConnectionHandler {

    /**
     * Checks if the socket is connected.
     *
     * @param socket the socket to check.
     * @return true if the socket is connected, false otherwise.
     */
    public static boolean check(Socket socket) {
        int countConn = 0;
        do {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            countConn++;
        } while (!socket.connected() && countConn <= 10);
        return socket.connected();
    }
}
