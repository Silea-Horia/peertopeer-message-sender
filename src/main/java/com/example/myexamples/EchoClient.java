package com.example.myexamples;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class EchoClient {
    public static void main(String[] args) {
        var scanner = new Scanner(System.in);
        System.out.println("Hostname:");
        var hostname = scanner.nextLine();
        System.out.println("Port:");
        var port = Integer.parseInt(scanner.nextLine());
        try (
                var echoSocket = new Socket(hostname, port);
                var out = new PrintWriter(echoSocket.getOutputStream(), true);
                var in = new BufferedReader(new InputStreamReader(echoSocket.getInputStream()));
                var stdIn = new BufferedReader(new InputStreamReader(System.in));
        ) {
            String userInput;
            while ((userInput = stdIn.readLine()) != null) {
                out.println(userInput);
                System.out.println("echo: " + in.readLine());
            }
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host " + hostname);
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to " + hostname);
            System.exit(1);
        }
    }
}
