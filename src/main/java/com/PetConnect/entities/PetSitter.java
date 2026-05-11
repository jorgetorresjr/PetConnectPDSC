package com.PetConnect.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_SITTER")
@DiscriminatorValue("PS")
@Getter
@Setter
public class PetSitter extends Usuario {

    
    public PetSitter() {
        super();
    }
}