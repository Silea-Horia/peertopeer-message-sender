package com.example.lifecycle;

import com.example.PeerToPeerApplication;
import com.example.listener.PeerToPeerListener;
import com.example.manager.ConnectionManager;
import com.example.reader.StdinReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ApplicationLifecycle {
    private static final Logger log = LogManager.getLogger(ApplicationLifecycle.class);
    private final ExecutorService executor = Executors.newCachedThreadPool();
    private final ConnectionManager connectionManager;
    private final PeerToPeerApplication application;

    public ApplicationLifecycle(ConnectionManager connectionManager, PeerToPeerApplication application) {
        this.connectionManager = connectionManager;
        this.application = application;
    }

    public void start(int port) {
        startConnectionManager();
        startStdinReader();
        startListener(port);
    }

    private void startConnectionManager()  {
        executor.execute(connectionManager);
    }

    private void startStdinReader() {
        executor.execute(new StdinReader(application));
    }

    private void startListener(int port) {
        executor.execute(new PeerToPeerListener(application, connectionManager, port));
    }

    public void shutdown() {
        log.warn("Closing all connections");
        connectionManager.exit();
        log.info("Bye.");
        System.exit(0);
    }
}
