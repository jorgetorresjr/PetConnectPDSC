
package com.PetConnect.entities;

import jakarta.persistence.DiscriminatorValue;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_OWNER")
@DiscriminatorValue("PO")
public class PetOwner extends User {
    
    public PetOwner() {
        super();
    }

        @Override
    public String getDiscriminator() {
        return "PO";
    }

}