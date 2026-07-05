package com.PetConnect.repositories;

import com.PetConnect.entities.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByAtivoTrue();
    Optional<Service> findByNomeIgnoreCase(String nome);
}