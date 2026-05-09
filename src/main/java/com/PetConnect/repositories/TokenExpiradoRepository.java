package com.PetConnect.repositories;

import com.PetConnect.entities.TokenExpirado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface TokenExpiradoRepository extends JpaRepository<TokenExpirado, Long> {
    boolean existsByToken(String token);
    void deleteByExpiresAtBefore(LocalDateTime now);
}