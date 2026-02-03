package com.example.myexamples;

import java.io.IOException;
import java.net.ServerSocket;

public class KKMultiServer {
    public static void main(String[] args) {
        var port = Integer.parseInt(args[0]);
        var listening = true;

        try (
                var serverSocket = new ServerSocket(port);
                ) {
            while (listening) {
                var clientSocket = serverSocket.accept();
                new KKMultiServerThread(clientSocket).start();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
