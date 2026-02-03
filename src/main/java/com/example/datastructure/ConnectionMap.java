package com.example.datastructure;

import com.example.model.Connection;
import com.example.model.ConnectionState;

import java.io.IOException;
import java.net.Socket;
import java.util.List;

public interface ConnectionMap {
    void addConnection(String name, Socket peer);

    Connection removeConnection(String name) throws IOException;

    Connection getConnection(String name);

    List<String> getAllNames();

    void setState(String name, ConnectionState newState);
}
