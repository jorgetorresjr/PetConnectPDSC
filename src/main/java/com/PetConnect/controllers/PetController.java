package com.PetConnect.controllers;

import com.PetConnect.DTOs.PetDTO;
import com.PetConnect.entities.Pet;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.PetRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;


    @PostMapping
    public ResponseEntity<?> createPet(
            @RequestParam String name,
            @RequestParam String specie,
            @RequestParam String breed,
            @RequestParam Integer age,
            @RequestParam(required = false) String observations,
            @RequestParam(required = false) MultipartFile photo
    ) {
        // Busca o dono autenticado
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        java.util.Optional<com.PetConnect.entities.PetOwner> ownerOpt = petOwnerRepository.findByEmail(email);
        if (ownerOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("Dono não encontrado.");
        }
        com.PetConnect.entities.PetOwner owner = ownerOpt.get();
        Pet pet = new Pet();
        pet.setName(name);
        pet.setSpecie(specie);
        pet.setBreed(breed);
        pet.setAge(age);
        pet.setObservations(observations);
        pet.setOwner(owner);
        try {
            if (photo != null && !photo.isEmpty()) {
                pet.setPhoto(photo.getBytes());
            }
        } catch (Exception ignored) {}
        petRepository.save(pet);
        return ResponseEntity.ok().build();
    }
    
}