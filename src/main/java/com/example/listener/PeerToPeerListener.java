package com.example.listener;

import com.example.PeerToPeerApplication;
import com.example.manager.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.ServerSocket;

public class PeerToPeerListener implements Runnable {
    private final PeerToPeerApplication application;
    private final ConnectionManager connectionManager;
    private final Logger log = LogManager.getLogger(PeerToPeerListener.class);
    private final int port;

    public PeerToPeerListener(PeerToPeerApplication application, ConnectionManager connectionManager, int port) {
        this.application = application;
        this.connectionManager = connectionManager;
        this.port = port;
    }

    @Override
    public void run() {
        try (var serverSocket = new ServerSocket(port)) {
            log.info("Listening for connections...");
            while (true) {
                try {
                    acceptConnections(serverSocket);
                } catch (IOException e) {
                    log.error("Error listening to connections");
                    log.debug(e.getMessage());
                    break;
                }
            }
        } catch (IOException e) {
            log.error("Error creating socket");
            log.debug(e.getMessage());
        }
        application.exit();
    }

    private void acceptConnections(ServerSocket serverSocket) throws IOException {
        var clientSocket = serverSocket.accept();
        connectionManager.receiveConnection(clientSocket);
    }
}
