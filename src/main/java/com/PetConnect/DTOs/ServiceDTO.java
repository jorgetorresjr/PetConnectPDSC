package com.PetConnect.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record ServiceDTO(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres.")
    String nome,

    @Size(max = 255, message = "A descrição deve ter no máximo 255 caracteres.")
    String descricao,

    @NotNull(message = "O preço base é obrigatório.")
    @Positive(message = "O preço base deve ser positivo.")
    BigDecimal precoBase
) {}
