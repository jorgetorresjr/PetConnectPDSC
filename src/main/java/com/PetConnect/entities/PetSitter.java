package com.PetConnect.entities;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_SITTER")
@DiscriminatorValue("PS")
@Getter 
@Setter 
@NoArgsConstructor
public class PetSitter extends Usuario {

}