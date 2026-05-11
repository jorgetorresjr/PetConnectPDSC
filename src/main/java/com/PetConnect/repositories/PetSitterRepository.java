package com.PetConnect.repositories;

import com.PetConnect.entities.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {
}