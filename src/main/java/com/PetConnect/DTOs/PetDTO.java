package com.PetConnect.DTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

public record PetDTO(
    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "O nome deve conter apenas letras, espaços e hífens.")
    String nome,
    @NotBlank(message = "A espécie é obrigatória.")
    @Size(min = 2, max = 100, message = "A espécie deve ter entre 2 e 100 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]+$", message = "A espécie deve conter apenas letras, espaços e hífens.")
    String especie,
    @Size(max = 100, message = "A raça deve ter no máximo 100 caracteres.")
    @Pattern(regexp = "^[A-Za-zÀ-ÿ\\s-]*$", message = "A raça deve conter apenas letras, espaços e hífens.")
    String raca,
    @NotNull(message = "A idade é obrigatória.")
    Integer idade,
    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
    String observacoes
) {}