package com.example.myexamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class KnockKnockClient {
    public static void main(String[] args) {
        var hostname = args[0];
        var port = Integer.parseInt(args[1]);

        try (
                var kksocket = new Socket(hostname, port);
                var out = new PrintWriter(kksocket.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(kksocket.getInputStream()));
                ) {
                var stdIn = new BufferedReader(new InputStreamReader(System.in));
                String fromServer, fromUser;

                while ((fromServer = in.readLine()) != null) {
                    System.out.println("Server: " + fromServer);

                    if (fromServer.equals("Bye.")) {
                        break;
                    }

                    fromUser = stdIn.readLine();
                    if (fromUser != null) {
                        System.out.println("Client: " + fromUser);
                        out.println(fromUser);
                    }
                }
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
