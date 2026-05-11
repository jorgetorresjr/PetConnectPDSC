package com.PetConnect.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_OWNER")
@DiscriminatorValue("PO")
@Getter
@Setter
public class PetOwner extends Usuario {

    public PetOwner() {
        super();
    }
}