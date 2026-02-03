package com.example.manager;

import com.example.sender.PeerToPeerSender;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class SenderCoordinator implements SenderManager {
    private final ExecutorService executor = Executors.newCachedThreadPool();

    @Override
    public void startSender(String message, ConnectionManager connectionManager) {
        executor.execute(new PeerToPeerSender(connectionManager, message));
    }
}
