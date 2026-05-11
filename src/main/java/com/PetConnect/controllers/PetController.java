package com.PetConnect.controllers;

import com.PetConnect.entities.Pet;
import com.PetConnect.entities.DTOs.PetDTO;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.PetRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/pets")
public class PetController {

    @Autowired
    private PetRepository petRepository;

    @Autowired
    private PetOwnerRepository petOwnerRepository;

    @PostMapping("/register")
    public ResponseEntity<?> registerPet(@RequestBody @Valid PetDTO data) {
        // Busca o dono no banco para garantir que ele existe
        var owner = petOwnerRepository.findById(data.ownerId());
        
        if (owner.isEmpty()) {
            return ResponseEntity.badRequest().body("Dono não encontrado.");
        }

        Pet novoPet = new Pet();
        novoPet.setNome(data.nome());
        novoPet.setEspecie(data.especie());
        novoPet.setRaca(data.raca());
        novoPet.setIdade(data.idade());
        novoPet.setObservacoes(data.observacoes());
        novoPet.setOwner(owner.get()); // Faz a ligação entre Pet e Dono

        petRepository.save(novoPet);

        return ResponseEntity.ok().build();
    }
}