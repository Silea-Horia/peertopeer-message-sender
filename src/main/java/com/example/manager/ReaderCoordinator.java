package com.example.manager;

import com.example.reader.PeerToPeerReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ReaderCoordinator implements ReaderManager {
    private static final Logger log = LogManager.getLogger(ReaderCoordinator.class);
    private final Map<String, ExecutorService> readers = new ConcurrentHashMap<>();

    @Override
    public void startNewReader(String connectionName, ConnectionManager connectionManager) {
        readers.put(connectionName, Executors.newSingleThreadExecutor());
        readers.get(connectionName).execute(new PeerToPeerReader(connectionManager, connectionName));
    }

    @Override
    public void removeReader(String connectionName) {
        try (var ex = readers.remove(connectionName)) {
            ex.shutdownNow();
        } catch (RuntimeException e) {
            log.error(e.getMessage());
        }
    }
}
