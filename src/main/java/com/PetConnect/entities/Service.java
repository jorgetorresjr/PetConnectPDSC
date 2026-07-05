package com.PetConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "TB_SERVICO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "TXT_NOME", nullable = false)
    private String nome;

    @Column(name = "TXT_DESCRICAO")
    private String descricao;

    @NotNull
    @Positive
    @Column(name = "NUM_PRECO_BASE", nullable = false)
    private BigDecimal precoBase;

    @Column(name = "ATIVO", nullable = false, columnDefinition = "BOOLEAN DEFAULT TRUE")
    private boolean ativo = true;
}