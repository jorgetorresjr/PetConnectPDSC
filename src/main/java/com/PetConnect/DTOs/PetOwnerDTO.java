package com.PetConnect.DTOs;

import com.PetConnect.entities.PetOwner;

public record PetOwnerDTO(Long id, String nome, String email, String telefone) {

    public PetOwnerDTO(PetOwner petOwner) {
        this(
            petOwner.getId(), 
            petOwner.getName(),   
            petOwner.getEmail(),   
            petOwner.getPhone()    
        );
    }
}