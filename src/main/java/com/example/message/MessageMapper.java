package com.example.message;

import com.example.model.Packet;
import tools.jackson.databind.ObjectMapper;

public class MessageMapper {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String mapToJson(String message) {
        var packet = new Packet(message);
        return mapper.writeValueAsString(packet);
    }

    public static String extractMessageFromJson(String json) {
        var packet = mapper.readValue(json, Packet.class);
        return packet.message();
    }
}
