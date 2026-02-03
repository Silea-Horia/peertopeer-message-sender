package com.example;

import com.example.lifecycle.ApplicationLifecycle;
import com.example.manager.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PeerToPeerApplication {
    private static final Logger log = LogManager.getLogger(PeerToPeerApplication.class);
    private final ReaderManager readerManager = new ReaderCoordinator();
    private final ConnectionManager connectionManager = new ConnectionManager(readerManager);
    private final SenderManager senderManager = new SenderCoordinator();
    private final ApplicationLifecycle appLifecycle = new ApplicationLifecycle(connectionManager, this);

    public static void main(String[] args) {
        new PeerToPeerApplication().run(args);
    }

    private void run(String[] args) {
        checkArgs(args);

        int port = extractPortFromArg(args[0]);

        appLifecycle.start(port);
    }

    private void checkArgs(String[] args) {
        if (args.length < 1) {
            log.error("Error: incorrect usage (should have port as arg).");
            System.exit(1);
        }
    }

    private int extractPortFromArg(String arg) {
        try {
            return Integer.parseInt(arg);
        } catch (NumberFormatException e) {
            log.error("Error: port should be a number!");
            log.debug(e.getMessage());
            System.exit(1);
        }
        return 1234;
    }

    public void exit() {
        appLifecycle.shutdown();
    }

    public void displayAllConnections() {
        var names = connectionManager.getAllConnectionNames();
        if (names.isEmpty()) {
            log.info("No connections");
        } else {
            names.stream()
                .map(name -> name + ":" + connectionManager.getConnection(name).state())
                .forEach(log::info);
        }
    }

    public void startSender(String message) {
        senderManager.startSender(message, connectionManager);
    }
}