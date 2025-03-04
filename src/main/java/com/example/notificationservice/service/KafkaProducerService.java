package com.example.notificationservice.service;

import com.example.notificationservice.dto.UserDto;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class KafkaProducerService {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaProducerService(KafkaTemplate<String, String> kafkaTemplate,
                                ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
    }

    public void sendMessage(List<UserDto> userDtos) {
        try {
            String jsonMessage = objectMapper.writeValueAsString(userDtos);
            kafkaTemplate.send("notification-topic", jsonMessage);
            System.out.println(" Message sent: " + jsonMessage);
        } catch (JsonProcessingException e) {
            System.out.println(e.getMessage());
        }
    }


}
