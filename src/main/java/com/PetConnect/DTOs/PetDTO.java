package com.PetConnect.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record PetDTO(
    @NotBlank String nome,
    @NotBlank String especie,
    String raca,
    @NotNull Integer idade,
    String observacoes,
    @NotNull Long ownerId // Precisamos saber quem é o dono
) {}