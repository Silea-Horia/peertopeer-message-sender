package com.example.manager;

import com.example.datastructure.ConcurrentConnectionMap;
import com.example.datastructure.ConnectionMap;
import com.example.model.Connection;
import com.example.model.ConnectionState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;
import java.util.List;
import java.util.Optional;

public class ConnectionManager implements Runnable {
    private final ConnectionMap connections = new ConcurrentConnectionMap();
    private final Logger log = LogManager.getLogger(ConnectionManager.class);
    private boolean running = true;
    private final ReaderManager readerManager;

    public ConnectionManager(ReaderManager readerManager) {
        this.readerManager = readerManager;
    }

    @Override
    public synchronized void run() {
        while (running) {
            try {
                this.wait(2000);
                cleanupDeadConnections();
            } catch (InterruptedException e) {
                return;
            }
        }
    }

    private synchronized void cleanupDeadConnections() {
        connections.getAllNames().forEach( name -> {
            var connection = connections.getConnection(name);
            var socket = connection.socket();
            if (socket.isInputShutdown() || socket.isOutputShutdown() || socket.isClosed() || !(isActiveConnection(name) || isConnectionInSetup(name))) {
                log.warn("Closed connection with {}", name);
                gracefulShutdown(name);
            }
        });
    }

    private void gracefulShutdown(String name) {
        try {
            var connection = connections.removeConnection(name);
            if (connection != null) {
                var socket = connection.socket();
                if (socket != null && !socket.isClosed()) {
                    socket.shutdownOutput();
                    socket.close();
                }
            }
            readerManager.removeReader(name);
        } catch (IOException e) {
            log.error("Error closing socket for {}: {}", name, e.getMessage());
        }
    }

    public void receiveConnection(Socket peer) {
        log.info("Incoming connection from {}:{}", peer.getInetAddress().getHostName(), peer.getPort());
        var name = generateName(peer);
        connections.addConnection(name, peer);
        readerManager.startNewReader(name, this);
    }

    private String generateName(Socket peer) {
        var hostname = peer.getInetAddress().getHostName();
        var port = peer.getPort();
        return hostname.concat(":").concat(String.valueOf(port));
    }

    public Optional<String> createConnection(String connectionName) {
        try {
            var peer = createSocketFromString(connectionName);
            connections.addConnection(connectionName, peer);
            readerManager.startNewReader(connectionName, this);
            return Optional.of(connectionName);
        } catch (SocketException e) {
            log.error(e.getMessage());
        }
        return Optional.empty();
    }

    private Socket createSocketFromString(String connectionName) throws SocketException {
        try {
            var parts = connectionName.split(":");
            var hostname = parts[0];
            var port = Integer.parseInt(parts[1]);
            return new Socket(hostname, port);
        } catch (IOException e) {
            throw new SocketException("Error creating connection");
        } catch (IndexOutOfBoundsException | NumberFormatException e) {
            throw new SocketException("Invalid name format. Expected <hostname:port>");
        }
    }

    public void markAccepted(String connectionName) {
        setConnectionState(connectionName, ConnectionState.ACTIVE);
    }

    public synchronized void markClosed(String connectionName) {
        try {
            setConnectionState(connectionName, ConnectionState.CLOSED);
        } catch (NullPointerException e) {
            log.debug("Error marking socket closed for {}: {}", connectionName, e.getMessage());
        }
    }

    private void setConnectionState(String name, ConnectionState newState) {
        try {
            connections.setState(name, newState);
        } catch (NullPointerException e) {
            log.debug("Error setting connection state");
        }
    }

    public void closeAllConnections() {
        connections.getAllNames().forEach(this::markClosed);
    }

    public synchronized void exit() {
        running = false;
        closeAllConnections();
        this.notifyAll();
    }

    public Socket getSocket(String name) {
        return connections.getConnection(name).socket();
    }

    public Connection getConnection(String name) {
        return connections.getConnection(name);
    }

    public List<String> getAllConnectionNames() {
        return connections.getAllNames();
    }

    public boolean isActiveConnection(String name) {
        var connection = connections.getConnection(name);
        if (connection != null) {
            try {
                return connection.state().equals(ConnectionState.ACTIVE)
                        && !connection.socket().isClosed();
            } catch (NullPointerException e) {
                log.error(e);
            }
        }
        return false;
    }

    public boolean isConnectionInSetup(String name) {
        var connection = connections.getConnection(name);
        if (connection != null) {
            try {
                return connections.getConnection(name).state().equals(ConnectionState.SETUP);
            } catch (NullPointerException e) {
                log.error(e);
                return false;
            }
        }
        return false;
    }
}
