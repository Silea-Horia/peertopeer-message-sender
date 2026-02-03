package com.example.sender;

import com.example.manager.ConnectionManager;
import com.example.message.MessageMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.PrintWriter;

public record PeerToPeerSender(ConnectionManager connectionManager, String text) implements Runnable {
    private static final Logger log = LogManager.getLogger(PeerToPeerSender.class);

    @Override
    public void run() {
        if (text.startsWith("!hello")) {
            handleHello(text);
            return;
        }

        if (text.startsWith("!ack")) {
            handleAck(text);
            return;
        }

        if (text.startsWith("!bye")) {
            handleBye(text);
            return;
        }

        handleGenericMessage(text);
    }

    private void handleHello(String messageText) {
        String destination;
        try {
            destination = messageText.substring("!hello".length() + 1);
        } catch (IndexOutOfBoundsException e) {
            log.warn("Invalid message format. Expected !hello,<name>");
            return;
        }
        var connectionName = connectionManager.createConnection(destination);
        connectionName.ifPresent(s -> sendMessage(s, messageText));
        connectionName.ifPresent(s -> log.info("Sent connection request to {}", s));
    }

    private void handleAck(String messageText) {
        try {
            var connectionName = messageText.substring("!ack".length() + 1);
            connectionManager.markAccepted(connectionName);
            sendMessage(connectionName, "!ack");
            log.info("Accepted connection with {}", connectionName);
        } catch (IndexOutOfBoundsException e) {
            log.warn("Invalid message format. Expected !ack,<name>");
        }
    }

    private void handleBye(String messageText) {
        try {
            var connectionName = messageText.substring("!bye".length() + 1);
            sendMessage(connectionName, "!bye");
        } catch (IndexOutOfBoundsException e) {
            log.warn("Invalid message format. Expected !bye,<name>");
        }
    }

    private void handleGenericMessage(String messageText) {
        try {
            var commaIndex = messageText.indexOf(',');
            var connectionName = messageText.substring(0, commaIndex).trim();
            var message = messageText.substring(commaIndex + 1).trim();
            sendMessage(connectionName, message);
        } catch (IndexOutOfBoundsException e) {
            log.warn("Invalid message format, expected 'connectionName,message' got: {}", text);
        }
    }

    private void sendMessage(String connectionName, String message) {
        try {
            var socket = connectionManager.getSocket(connectionName);
            var out = new PrintWriter(socket.getOutputStream(), true);

            log.debug("Sending... {}", message);

            var json = MessageMapper.mapToJson(message);

            log.debug("Json format: {}", json);

            out.println(json);
            out.flush();

            // refuse connection
            if (connectionManager.isConnectionInSetup(connectionName) && !message.startsWith("!")) {
                connectionManager.markClosed(connectionName);
            }

            synchronized (connectionManager) {
                connectionManager.notify();
            }
        } catch (IOException e) {
            log.error("Error sending message");
            log.debug(e.getMessage());
            connectionManager.markClosed(connectionName);
        } catch (NullPointerException e) {
            log.error("Connection doesn't exist");
        }
    }
}
