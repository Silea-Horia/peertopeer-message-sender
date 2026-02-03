package com.example.myexamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;

public class KnockKnockServer {
    public static void main(String[] args) {
        System.out.println("This is the server");
        var port = Integer.parseInt(args[0]);

        try (
                var serverSocker = new ServerSocket(port);
                var clientSocket = serverSocker.accept();
                var out = new PrintWriter(clientSocket.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
            System.out.println("Listening on port: " + port);
            String inputLine, outputLine;
            var kkp = new KnockKnockProtocol();
            outputLine = kkp.processInput(null);
            out.println(outputLine);

            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                outputLine = kkp.processInput(inputLine);
                out.println(outputLine);
                if (outputLine.equals("Bye.")) {
                    break;
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
