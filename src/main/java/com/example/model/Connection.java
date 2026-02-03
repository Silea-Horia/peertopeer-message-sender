package com.example.model;

import java.net.Socket;

public record Connection(Socket socket, ConnectionState state) { }
