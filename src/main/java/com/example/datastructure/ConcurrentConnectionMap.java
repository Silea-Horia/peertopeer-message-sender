package com.example.datastructure;

import com.example.model.Connection;
import com.example.model.ConnectionState;

import java.net.Socket;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentConnectionMap implements ConnectionMap {
    private final Map<String, Connection> connectionsByName = new ConcurrentHashMap<>();

    @Override
    public void addConnection(String name, Socket peer) {
        connectionsByName.put(name, new Connection(peer, ConnectionState.SETUP));
    }

    @Override
    public Connection removeConnection(String name) {
        return connectionsByName.remove(name);
    }

    @Override
    public Connection getConnection(String name) {
        return connectionsByName.get(name);
    }

    @Override
    public List<String> getAllNames() {
        return connectionsByName.keySet().stream().toList();
    }

    @Override
    public void setState(String name, ConnectionState newState) {
        connectionsByName.putIfAbsent(name, new Connection(connectionsByName.get(name).socket(), newState));
    }
}
