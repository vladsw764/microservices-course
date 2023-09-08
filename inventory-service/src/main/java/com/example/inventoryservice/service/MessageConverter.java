package com.example.inventoryservice.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MessageConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> convertJsonToMap(String jsonMessage) {
        try {
            String[] jsonArray = objectMapper.readValue(jsonMessage, String[].class);

            String jsonString = jsonArray[0];

            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {
            });
        } catch (IOException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}

