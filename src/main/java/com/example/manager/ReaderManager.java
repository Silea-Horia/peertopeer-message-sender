package com.example.manager;

public interface ReaderManager {
    void startNewReader(String connectionName, ConnectionManager connectionManager);
    void removeReader(String connectionName);
}
