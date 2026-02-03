package com.example.myexamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.util.Scanner;

public class EchoServer {
    public static void main(String[] args) {
        System.out.println("Port:");
        var port = Integer.parseInt(new Scanner(System.in).nextLine());
        try (
                var serverSocket = new ServerSocket(port);
                var clientSocket = serverSocket.accept();
                var out = new PrintWriter(clientSocket.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                ) {
            System.out.println("Listening on: " + port);
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                System.out.println("Received: " + inputLine);
                out.println(inputLine);
            }
        } catch (IOException e) {
            System.out.println("Exception caught when trying to listen on port "
                    + port + " or listening for a connection");
            System.out.println(e.getMessage());
        }
    }
}
