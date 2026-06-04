package com.PetConnect.controllers;

import com.PetConnect.entities.PetSitter;
import com.PetConnect.repositories.PetSitterRepository;
import com.PetConnect.repositories.UserRepository;
import com.PetConnect.repositories.ServiceRepository;
import com.PetConnect.entities.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

@RestController
@RequestMapping("/petsitters")
public class PetSitterController {
    @Autowired
    private PetSitterRepository petSitterRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ServiceRepository serviceRepository;

    @PutMapping("/profile")
    public ResponseEntity<PetSitter> updateProfile(
        @RequestParam String specialty,
        @RequestParam(required = false) String certificates,
        @RequestParam(required = false) List<Long> servicesIds,
        @RequestParam(required = false) String dias,
        @RequestParam(required = false) String horarioInicio,
        @RequestParam(required = false) String horarioFim,
        @RequestParam(required = false) String servicePrices,
        @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        var user = userOpt.get();
        PetSitter petSitter;
        if (user instanceof PetSitter) {
            petSitter = (PetSitter) user;
        } else {
            petSitter = new PetSitter();
            petSitter.setId(user.getId());
            petSitter.setName(user.getName());
            petSitter.setEmail(user.getEmail());
            petSitter.setPhone(user.getPhone());
            petSitter.setCpf(user.getCpf());
            petSitter.setLogin(user.getLogin());
            petSitter.setPassword(user.getPassword());
            petSitter.setBirthDate(user.getBirthDate());
            petSitter.setAddress(user.getAddress());
            petSitter.setPhoto(user.getPhoto());
        }
        petSitter.setSpecialty(specialty);
        petSitter.setCertificates(certificates);
        // Associar serviços selecionados
        if (servicesIds != null && !servicesIds.isEmpty()) {
            List<Service> selectedServices = serviceRepository.findAllById(servicesIds);
            petSitter.setServices(selectedServices);
        }
        // Só salva disponibilidade se houver dias marcados e horários preenchidos
        if (dias != null && !dias.equals("[]") && horarioInicio != null && !horarioInicio.isBlank() && horarioFim != null && !horarioFim.isBlank()) {
            String disponibilidade = dias + "|" + horarioInicio + "-" + horarioFim;
            petSitter.setAvailability(disponibilidade);
        } else {
            petSitter.setAvailability(null);
        }
        if (servicePrices != null) {
            petSitter.setServicePrices(servicePrices);
        }
        if (photo != null && !photo.isEmpty()) {
            petSitter.setPhoto(photo.getBytes());
        }
        PetSitter saved = petSitterRepository.save(petSitter);
        return ResponseEntity.ok(saved);
    }

    @GetMapping
    public List<PetSitter> listAll() {
        return petSitterRepository.findAll();
    }

    @GetMapping("/me")
    public ResponseEntity<PetSitter> getMyProfile() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isPresent() && userOpt.get() instanceof PetSitter) {
            return ResponseEntity.ok((PetSitter) userOpt.get());
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/filter")
    public ResponseEntity<List<PetSitter>> filterByService(@RequestParam Long serviceId) {
        return ResponseEntity.ok(petSitterRepository.findByServiceId(serviceId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PetSitter> getById(@PathVariable Long id) {
        return petSitterRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
