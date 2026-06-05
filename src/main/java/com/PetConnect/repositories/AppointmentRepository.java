package com.PetConnect.repositories;

import com.PetConnect.entities.Appointment;
import com.PetConnect.entities.enums.AppointmentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppointmentRepository extends JpaRepository<Appointment, Long> {

    List<Appointment> findByPetSitterIdOrderByCreatedAtDesc(Long petSitterId);

    List<Appointment> findByPetOwnerIdOrderByCreatedAtDesc(Long petOwnerId);

    List<Appointment> findByPetSitterIdAndStatusOrderByCreatedAtDesc(Long petSitterId, AppointmentStatus status);

    List<Appointment> findByPetSitterEmailOrderByCreatedAtDesc(String email);
}