package com.PetConnect.controllers;

import com.PetConnect.DTOs.PetDTO;
import com.PetConnect.entities.Pet;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.PetRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @PostMapping
    public ResponseEntity<?> createPet(
            @ModelAttribute @Valid PetDTO petDTO,
            BindingResult bindingResult,
            @RequestParam(required = false) MultipartFile photo
    ) {
        // Validar erros do DTO
        if (bindingResult.hasErrors()) {
            List<Map<String, String>> errors = bindingResult.getFieldErrors().stream()
                    .map(e -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("field", e.getField());
                        error.put("defaultMessage", e.getDefaultMessage());
                        return error;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        // Busca o dono autenticado
        org.springframework.security.core.Authentication authentication = 
            org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        java.util.Optional<com.PetConnect.entities.PetOwner> ownerOpt = 
            petOwnerRepository.findByEmail(email);
        
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Dono não encontrado.");
        }

        com.PetConnect.entities.PetOwner owner = ownerOpt.get();
        Pet pet = new Pet();
        pet.setName(petDTO.nome());
        pet.setSpecie(petDTO.especie());
        pet.setBreed(petDTO.raca());
        pet.setAge(petDTO.idade());
        pet.setObservations(petDTO.observacoes());
        pet.setOwner(owner);

        try {
            if (photo != null && !photo.isEmpty()) {
                pet.setPhoto(photo.getBytes());
            }
        } catch (Exception ignored) {}

        try {
            petRepository.save(pet);
        } catch (jakarta.validation.ConstraintViolationException e) {
            // Capturar erros de validação da entidade Pet
            List<Map<String, String>> errors = e.getConstraintViolations().stream()
                    .map(cv -> {
                        Map<String, String> error = new HashMap<>();
                        error.put("field", cv.getPropertyPath().toString());
                        error.put("defaultMessage", cv.getMessage());
                        return error;
                    })
                    .collect(Collectors.toList());
            return ResponseEntity.badRequest().body(Map.of("errors", errors));
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Pet> getById(@PathVariable Long id) {
        return petRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/my")
    public ResponseEntity<?> listMyPets() {
        org.springframework.security.core.Authentication authentication =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        java.util.Optional<com.PetConnect.entities.PetOwner> ownerOpt = 
            petOwnerRepository.findByEmail(email);
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.status(403).body("Apenas tutor pode visualizar seus pets.");
        }

        java.util.List<Pet> pets = petRepository.findByOwnerId(ownerOpt.get().getId());
        return ResponseEntity.ok(pets);
    }
}