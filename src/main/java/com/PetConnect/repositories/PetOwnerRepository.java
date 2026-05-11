package com.PetConnect.repositories;

import com.PetConnect.entities.PetOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PetOwnerRepository extends JpaRepository<PetOwner, Long> {
	java.util.Optional<PetOwner> findByEmail(String email);
}