package com.PetConnect.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_SITTER")
@Getter @Setter @NoArgsConstructor
public class PetSitter extends Usuario {
    //adicionar campos no futuro
}