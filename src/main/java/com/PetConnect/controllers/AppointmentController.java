package com.PetConnect.controllers;

import com.PetConnect.entities.Appointment;
import com.PetConnect.entities.Pet;
import com.PetConnect.entities.PetOwner;
import com.PetConnect.entities.PetSitter;
import com.PetConnect.entities.Service;
import com.PetConnect.entities.User;
import com.PetConnect.entities.enums.AppointmentStatus;
import com.PetConnect.repositories.AppointmentRepository;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.PetRepository;
import com.PetConnect.repositories.PetSitterRepository;
import com.PetConnect.repositories.ServiceRepository;
import com.PetConnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/appointments")
public class AppointmentController {

    @Autowired
    private AppointmentRepository appointmentRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @Autowired
    private PetSitterRepository petSitterRepository;

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @PostMapping
    public ResponseEntity<?> createAppointment(
            @RequestParam(required = false) Long petSitterId,
            @RequestParam(required = false) Long serviceId,
            @RequestParam(required = false) Long petId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate serviceDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime serviceTime) {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Optional<PetOwner> ownerOpt = petOwnerRepository.findByEmail(email);
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Apenas tutor pode solicitar agendamento.");
        }

        if (petSitterId == null) {
            return ResponseEntity.badRequest().body("Pet sitter é obrigatório.");
        }

        if (serviceId == null) {
            return ResponseEntity.badRequest().body("Serviço é obrigatório.");
        }

        if (petId == null) {
            return ResponseEntity.badRequest().body("Selecione um pet para agendar.");
        }

        if (serviceDate == null) {
            return ResponseEntity.badRequest().body("Data do serviço é obrigatória.");
        }

        if (serviceTime == null) {
            return ResponseEntity.badRequest().body("Horário do serviço é obrigatório.");
        }

        Optional<Pet> petOpt = petRepository.findById(petId);
        if (petOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Pet não encontrado.");
        }

        Optional<PetSitter> sitterOpt = petSitterRepository.findById(petSitterId);
        if (sitterOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Pet sitter não encontrado.");
        }

        Optional<Service> serviceOpt = serviceRepository.findById(serviceId);
        if (serviceOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Serviço não encontrado.");
        }

        PetOwner owner = ownerOpt.get();
        Pet pet = petOpt.get();
        PetSitter sitter = sitterOpt.get();
        Service service = serviceOpt.get();

        if (pet.getOwner() == null || !pet.getOwner().getId().equals(owner.getId())) {
            return ResponseEntity.status(403).body("Você só pode agendar usando um pet seu.");
        }

        boolean serviceOffered = sitter.getServices() != null
                && sitter.getServices().stream().anyMatch(s -> s.getId().equals(service.getId()));

        if (!serviceOffered) {
            return ResponseEntity.badRequest().body("O serviço selecionado não é oferecido por este pet sitter.");
        }

        LocalDateTime agendamento = LocalDateTime.of(serviceDate, serviceTime);
        if (!agendamento.isAfter(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("A data e hora do agendamento devem ser no futuro.");
        }

        Appointment appointment = new Appointment();
        appointment.setPetOwner(owner);
        appointment.setPetSitter(sitter);
        appointment.setPet(pet);
        appointment.setService(service);
        appointment.setServiceDate(serviceDate);
        appointment.setServiceTime(serviceTime);
        appointment.setStatus(AppointmentStatus.PENDENTE);

        Appointment saved = appointmentRepository.save(appointment);
        return ResponseEntity.ok(toResponse(saved));
    }

    @GetMapping("/petsitter")
    public ResponseEntity<List<Map<String, Object>>> listForPetSitter() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        List<Map<String, Object>> response = appointmentRepository
                .findByPetSitterEmailOrderByCreatedAtDesc(email)
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Map<String, Object>>> listForPetOwner() {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Optional<PetOwner> ownerOpt = petOwnerRepository.findByEmail(email);
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(403).build();
        }

        List<Map<String, Object>> response = appointmentRepository
                .findByPetOwnerIdOrderByCreatedAtDesc(ownerOpt.get().getId())
                .stream()
                .map(this::toResponse)
                .toList();

        return ResponseEntity.ok(response);
    }

   @PutMapping("/{id}/status")
public ResponseEntity<?> updateAppointmentStatus(
        @PathVariable Long id,
        @RequestParam AppointmentStatus status
) {
    System.out.println("[DEBUG] updateAppointmentStatus chamado - id=" + id + " status=" + status);
    if (status != AppointmentStatus.ACEITO && status != AppointmentStatus.RECUSADO) {
        return ResponseEntity.badRequest().body("Status inválido. Use ACEITO ou RECUSADO.");
    }

    Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
    if (appointmentOpt.isEmpty()) {
        return ResponseEntity.badRequest().body("Agendamento não encontrado.");
    } 

    Appointment appointment = appointmentOpt.get();

    if (appointment.getStatus() != AppointmentStatus.PENDENTE) {
        return ResponseEntity.badRequest().body("Somente solicitações PENDENTE podem ser alteradas.");
    }

    appointment.setStatus(status);
    Appointment saved = appointmentRepository.save(appointment);
    return ResponseEntity.ok(toResponse(saved));
}

private Map<String, Object> toResponse(Appointment a) {
    Map<String, Object> m = new LinkedHashMap<>();
    m.put("id", a.getId());
    m.put("status", a.getStatus());
    m.put("serviceDate", a.getServiceDate());
    m.put("serviceTime", a.getServiceTime());
    m.put("serviceId", a.getService() != null ? a.getService().getId() : null);
    m.put("serviceName", a.getService() != null ? a.getService().getNome() : null);
    m.put("petId", a.getPet() != null ? a.getPet().getId() : null);
    m.put("petName", a.getPet() != null ? a.getPet().getName() : null);
    m.put("petOwnerId", a.getPetOwner() != null ? a.getPetOwner().getId() : null);
    m.put("petOwnerName", a.getPetOwner() != null ? a.getPetOwner().getName() : null);
    m.put("petSitterId", a.getPetSitter() != null ? a.getPetSitter().getId() : null);
    m.put("petSitterName", a.getPetSitter() != null ? a.getPetSitter().getName() : null);
    m.put("createdAt", a.getCreatedAt());
    return m;
}
}