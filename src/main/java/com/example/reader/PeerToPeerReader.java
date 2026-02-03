package com.example.reader;

import com.example.manager.ConnectionManager;
import com.example.message.MessageMapper;
import com.example.protocol.ProtocolHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tools.jackson.core.JacksonException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public record PeerToPeerReader(ConnectionManager connectionManager, String name) implements Runnable {
    private static final Logger log = LogManager.getLogger(PeerToPeerReader.class);

    @Override
    public void run() {
        var protocolHandler = new ProtocolHandler(connectionManager, name);

        try (
                var socket = connectionManager.getSocket(name);
                var in = new BufferedReader(new InputStreamReader(socket.getInputStream()))
        ) {
            log.debug("Reading...");
            String json;
            while (connectionManager.isActiveConnection(name) || connectionManager.isConnectionInSetup(name)) {
                json = in.readLine();
                if (json == null) break;

                var message = MessageMapper.extractMessageFromJson(json);
                var action = protocolHandler.handleMessage(message);

                switch (action) {
                    case TERMINATE -> { return; }
                    case DISPLAY -> System.out.println("[" + name + "]: " + message);
                    case SKIP -> { }
                }
            }
        } catch (IOException e) {
            log.debug("Error receiving from peer: {}", e.getMessage());
        } catch (JacksonException e) {
            log.warn("Invalid json message format!");
        }

        connectionManager.markClosed(name);
        synchronized (connectionManager) {
            connectionManager.notify();
        }
    }

}
