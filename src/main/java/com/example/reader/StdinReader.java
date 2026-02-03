package com.example.reader;

import com.example.PeerToPeerApplication;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class StdinReader implements Runnable {
    private static final Logger log = LogManager.getLogger(StdinReader.class);
    private final PeerToPeerApplication application;

    public StdinReader(PeerToPeerApplication application) {
        this.application = application;
    }

    @Override
    public void run() {
        String message;
        var in = new BufferedReader(new InputStreamReader(System.in));
        while (true) {
            try {
                message = in.readLine();
                if (message != null) {
                    if (message.equalsIgnoreCase("!byebye")) {
                        exit();
                        break;
                    }
                    if (message.equalsIgnoreCase("!list")) {
                        application.displayAllConnections();
                        continue;
                    }
                    application.startSender(message);
                }
            } catch (IOException e) {
                log.fatal("Fatal error reading from stdin");
                exit();
                return;
            }
        }
    }

    private void exit() {
        log.debug("exiting...");
        application.exit();
    }
}
