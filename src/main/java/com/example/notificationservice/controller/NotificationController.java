package com.example.notificationservice.controller;

import com.example.notificationservice.dto.UserDto;
import com.example.notificationservice.service.KafkaProducerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notification")
public class NotificationController {
    private final KafkaProducerService kafkaProducerService;

    public NotificationController(KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
    }

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@Valid @RequestBody List<UserDto> userDtos) {  // ✅ Cần @Valid
        kafkaProducerService.sendMessage(userDtos);
        return ResponseEntity.ok("Emails sent successfully!");
    }
}
