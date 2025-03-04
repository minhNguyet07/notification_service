package com.example.notificationservice.entity;

import jakarta.persistence.*;
import lombok.*;

@Setter
@Getter
@Entity
@Table(name = "failed_emails")

@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FailedEmailEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String email;
    private String subject;
    @Column(columnDefinition = "TEXT")
    private String content;

    private int retryCount;

}
