package com.PetConnect.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "TB_PET_OWNER")
public class PetOwner extends Usuario {
    
    public PetOwner() {
        super();
    }
}