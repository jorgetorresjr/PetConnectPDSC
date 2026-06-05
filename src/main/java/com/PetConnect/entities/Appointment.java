package com.PetConnect.entities;

import com.PetConnect.entities.enums.AppointmentStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "TB_APPOINTMENT")
@Getter
@Setter
@NoArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PET_OWNER_ID", nullable = false)
    private PetOwner petOwner;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PET_SITTER_ID", nullable = false)
    private PetSitter petSitter;

    @ManyToOne(optional = false)
    @JoinColumn(name = "PET_ID", nullable = false)
    private Pet pet;

    @ManyToOne(optional = false)
    @JoinColumn(name = "SERVICE_ID", nullable = false)
    private Service service;

    @Column(name = "SERVICE_DATE", nullable = false)
    private LocalDate serviceDate;

    @Column(name = "SERVICE_TIME", nullable = false)
    private LocalTime serviceTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "STATUS", nullable = false, length = 20)
    private AppointmentStatus status = AppointmentStatus.PENDENTE;

    @Column(name = "CREATED_AT", nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void prePersist() {
        if (status == null) {
            status = AppointmentStatus.PENDENTE;
        }
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}