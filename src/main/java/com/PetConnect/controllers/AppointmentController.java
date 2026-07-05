package com.PetConnect.controllers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.time.format.DateTimeFormatter;

import com.PetConnect.DTOs.CreateAppointmentDTO;
import com.PetConnect.entities.Appointment;
import com.PetConnect.entities.Pet;
import com.PetConnect.entities.PetOwner;
import com.PetConnect.entities.PetSitter;
import com.PetConnect.entities.Service;
import com.PetConnect.entities.enums.AppointmentStatus;
import com.PetConnect.repositories.AppointmentRepository;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.PetRepository;
import com.PetConnect.repositories.PetSitterRepository;
import com.PetConnect.repositories.ServiceRepository;
import com.PetConnect.repositories.UserRepository;

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
    public ResponseEntity<?> createAppointment(@RequestBody CreateAppointmentDTO dto) {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Optional<PetOwner> ownerOpt = petOwnerRepository.findByEmail(email);
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Apenas tutor pode solicitar agendamento.");
        }

        // Converte as strings do JSON para os tipos que o Java entende
        LocalDate serviceDate = LocalDate.parse(dto.getServiceDate());
        LocalTime serviceTime = LocalTime.parse(dto.getServiceTime());

        Optional<Pet> petOpt = petRepository.findById(dto.getPetId());
        if (petOpt.isEmpty())
            return ResponseEntity.badRequest().body("Pet não encontrado.");

        Optional<PetSitter> sitterOpt = petSitterRepository.findById(dto.getPetSitterId());
        if (sitterOpt.isEmpty())
            return ResponseEntity.badRequest().body("Pet sitter não encontrado.");

        Optional<Service> serviceOpt = serviceRepository.findById(dto.getServiceId());
        if (serviceOpt.isEmpty())
            return ResponseEntity.badRequest().body("Serviço não encontrado.");

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
            return ResponseEntity.badRequest().body("O serviço selecionado não é oferecido por este profissional.");
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
            @RequestParam AppointmentStatus status) {
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

    private void appendStatusHistory(Appointment a, String displayStatus) {
        DateTimeFormatter df = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        DateTimeFormatter tf = DateTimeFormatter.ofPattern("HH:mm");
        String date = a.getServiceDate() != null ? a.getServiceDate().format(df) : "-";
        String time = a.getServiceTime() != null ? a.getServiceTime().format(tf) : "-";
        String entry = String.format("DATA: %s %s - %s", date, time, displayStatus);
        String hist = a.getStatusHistory();
        a.setStatusHistory((hist == null || hist.isEmpty()) ? entry : hist + "\n" + entry);
    }

    @PutMapping("/{id}/start")
    public ResponseEntity<?> startAppointment(@PathVariable Long id) {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty())
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");

        Appointment a = appointmentOpt.get();
        if (a.getPetSitter() == null || !email.equals(a.getPetSitter().getEmail())) {
            return ResponseEntity.status(403).body("Apenas o petsitter responsável pode iniciar.");
        }
        if (a.getStatus() != AppointmentStatus.ACEITO) {
            return ResponseEntity.badRequest().body("Somente agendamentos ACEITOS podem ser iniciados.");
        }

        a.setStatus(AppointmentStatus.EM_ANDAMENTO);
        appendStatusHistory(a, "EM ANDAMENTO");
        Appointment saved = appointmentRepository.save(a);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{id}/finish")
    public ResponseEntity<?> finishAppointment(@PathVariable Long id) {
        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext().getAuthentication().getName();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);
        if (appointmentOpt.isEmpty())
            return ResponseEntity.badRequest().body("Agendamento não encontrado.");

        Appointment a = appointmentOpt.get();
        if (a.getPetSitter() == null || !email.equals(a.getPetSitter().getEmail())) {
            return ResponseEntity.status(403).body("Apenas o petsitter responsável pode finalizar.");
        }
        if (a.getStatus() != AppointmentStatus.EM_ANDAMENTO) {
            return ResponseEntity.badRequest().body("Somente agendamentos EM ANDAMENTO podem ser finalizados.");
        }

        a.setStatus(AppointmentStatus.CONCLUIDO);
        appendStatusHistory(a, "FINALIZADO");
        Appointment saved = appointmentRepository.save(a);
        return ResponseEntity.ok(toResponse(saved));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<?> cancelAppointment(@PathVariable Long id) {

        String email = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        Optional<Appointment> appointmentOpt = appointmentRepository.findById(id);

        if (appointmentOpt.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body("Agendamento não encontrado.");
        }

        Appointment appointment = appointmentOpt.get();

        if (appointment.getPetOwner() == null ||
                !email.equals(appointment.getPetOwner().getEmail())) {

            return ResponseEntity.status(403)
                    .body("Apenas o tutor pode cancelar.");
        }

        if (appointment.getStatus() != AppointmentStatus.PENDENTE) {
            return ResponseEntity.badRequest()
                    .body("Somente agendamentos pendentes podem ser cancelados.");
        }

        appointment.setStatus(AppointmentStatus.RECUSADO);

        appendStatusHistory(
                appointment,
                "CANCELADO PELO TUTOR");

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
        m.put("history", a.getStatusHistory());
        return m;
    }
}