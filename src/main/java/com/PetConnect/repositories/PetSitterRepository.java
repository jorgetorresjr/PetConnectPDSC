package com.PetConnect.repositories;

import com.PetConnect.entities.PetSitter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

@Repository
public interface PetSitterRepository extends JpaRepository<PetSitter, Long> {

    @Query("SELECT ps FROM PetSitter ps JOIN ps.services s WHERE s.id = :serviceId")
    List<PetSitter> findByServiceId(@Param("serviceId") Long serviceId);

}