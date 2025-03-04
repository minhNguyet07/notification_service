package com.example.notificationservice.repository;

import com.example.notificationservice.entity.FailedEmailEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FailedEmailRepository extends JpaRepository<FailedEmailEntity, Long> {
    List<FailedEmailEntity> findByRetryCountLessThan(int maxRetries);
}
