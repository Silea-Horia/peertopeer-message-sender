package com.example.protocol;

import com.example.manager.ConnectionManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public record ProtocolHandler(ConnectionManager connectionManager, String connectionName) {
    private static final Logger log = LogManager.getLogger(ProtocolHandler.class);

    public ProtocolAction handleMessage(String message) {
        if (message == null) {
            log.warn("Received message with wrong format");
        }

        if (message.equalsIgnoreCase("!bye")) {
            connectionManager.markClosed(connectionName);
            return ProtocolAction.TERMINATE;
        }

        if (connectionManager.isConnectionInSetup(connectionName)) {
            return handleSetupPhase(message);
        }

        if (message.startsWith("!hello")) {
            return ProtocolAction.SKIP;
        }

        return ProtocolAction.DISPLAY;
    }

    private ProtocolAction handleSetupPhase(String message) {
        if (message.equalsIgnoreCase("!ack")) {
            log.info("Connection with {} accepted", connectionName);
            connectionManager.markAccepted(connectionName);
            return ProtocolAction.SKIP;
        }

        if (!message.startsWith("!hello")) {
            log.warn("Connection with {} refused", connectionName);
            return ProtocolAction.TERMINATE;
        }

        return ProtocolAction.SKIP;
    }

    public enum ProtocolAction {
        DISPLAY,
        SKIP,
        TERMINATE
    }
}