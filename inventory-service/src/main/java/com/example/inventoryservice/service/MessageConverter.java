package com.example.inventoryservice.service;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class MessageConverter {
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson ObjectMapper

    public Map<String, Object> convertJsonToMap(String jsonMessage) {
        try {            // Parse the JSON array containing a single string element
            String[] jsonArray = objectMapper.readValue(jsonMessage, String[].class);

            // Extract the string element
            String jsonString = jsonArray[0];

            // Convert the extracted string to a map
            return objectMapper.readValue(jsonString, new TypeReference<Map<String, Object>>() {});
        } catch (IOException e) {
            // Handle the exception as needed
            e.printStackTrace();
            return null; // Or throw an exception
        }
    }
}

