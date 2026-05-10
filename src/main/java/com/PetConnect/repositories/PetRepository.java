package com.PetConnect.repositories;

import com.PetConnect.entities.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PetRepository extends JpaRepository<Pet, Long> {
    // Busca todos os pets de um dono específico (útil para a tela de perfil)
    List<Pet> findByOwnerId(Long ownerId);
}