package com.example.notificationservice.service;

import com.example.notificationservice.dto.UserDto;
import com.example.notificationservice.entity.FailedEmailEntity;
import com.example.notificationservice.repository.FailedEmailRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service

public class KafkaConsumerService {
    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerService.class);
    private final ObjectMapper objectMapper;
    private final FailedEmailRepository failedEmailRepository;
    private static final int MAX_RETRIES = 3;
    private final ExecutorService executorService = Executors.newFixedThreadPool(5);
    @Value("${spring.mail.password}")
    private String mailPassword;

    @Value("${spring.mail.username}")
    private String username;

    public KafkaConsumerService(ObjectMapper objectMapper, FailedEmailRepository failedEmailRepository) {
        this.objectMapper = objectMapper;
        this.failedEmailRepository = failedEmailRepository;
    }

    private JavaMailSender configureMailSender(String username) {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);
        mailSender.setUsername(username);
        mailSender.setPassword(mailPassword);

        Properties properties = mailSender.getJavaMailProperties();
        properties.put("mail.smtp.auth", "true");
        properties.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @KafkaListener(topics = "notification-topic", groupId = "notification-group")
    public void listen(String messages) throws JsonProcessingException {

        List<UserDto> notifications = objectMapper.readValue(messages, new TypeReference<List<UserDto>>() {
        });
        notifications.forEach(message -> executorService.submit(() -> processMessage(message)));
    }

    private void processMessage(UserDto message) {
        try {
            processEmail(message);
        } catch (Exception e) {
            logger.error("Error processing message {}", message, e);
        }
    }

    private boolean sendEmail(String to, String subject, String text) {
        if (to == null || !to.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.(com|net|org|vn)$")) {
            logger.error("Invalid email format: {}", to);
            return false;
        }

        try {
            JavaMailSender mailSender = configureMailSender(username);
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(text, true);
            mailSender.send(message);
            logger.info("Email sent to {}", to);
            return true;
        } catch (MessagingException e) {
            logger.error("Failed to send email to {}: {}", to, e.getMessage(), e);
            return false;
        }
    }


    private void processEmail(UserDto userDto) {
        boolean success = sendEmail(userDto.getEmail(), "Notification", userDto.getMessage());
        if (!success) {
            saveFailedEmail(userDto);
        }
    }


    private void saveFailedEmail(UserDto userDto) {
        FailedEmailEntity failedEmail = new FailedEmailEntity();
        failedEmail.setEmail(userDto.getEmail());
        failedEmail.setSubject("Notification");
        failedEmail.setContent(userDto.getMessage());
        failedEmail.setRetryCount(0);

        failedEmailRepository.save(failedEmail);
        logger.info("Saved failed email to database for retry: {}", userDto.getEmail());
    }

    @Scheduled(cron = "0 * * * * ?")
    public void retryFailedEmails() {
        logger.info("Scheduled task is running.");
        List<FailedEmailEntity> failedEmails = failedEmailRepository.findByRetryCountLessThan(MAX_RETRIES);
        if (failedEmails.isEmpty()) {
            return;
        }

        for (FailedEmailEntity failedEmail : failedEmails) {
            boolean success = sendEmail(failedEmail.getEmail(), failedEmail.getSubject(), failedEmail.getContent());
            if (success) {
                failedEmailRepository.delete(failedEmail);
            } else {
                failedEmail.setRetryCount(failedEmail.getRetryCount() + 1);
                failedEmailRepository.save(failedEmail);
            }
        }
    }

}
