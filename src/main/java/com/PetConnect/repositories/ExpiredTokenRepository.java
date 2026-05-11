package com.PetConnect.repositories;

import com.PetConnect.entities.ExpiredToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface ExpiredTokenRepository extends JpaRepository<ExpiredToken, Long> {
    boolean existsByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime now);
}