package com.PetConnect.entities.DTOs;

import com.PetConnect.entities.Endereco;
import com.PetConnect.entities.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Date;
import java.util.List;

public record CadastroDTO(
        @Valid
        @NotNull
        Endereco endereco,

        String telefone,

        @CPF
        @NotBlank
        String cpf,

        @NotBlank
        String login,

        @NotBlank
        @Pattern(
                regexp = "^[A-ZÀ-Ú][a-zà-ú]+(?:\\s[A-ZÀ-Ú][a-zà-ú]+)*$",
                message = "Nome inválido"
        )
        String nome,

        @Email
        @NotBlank
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
                message = "Senha deve conter letra maiúscula, minúscula, número e caractere especial"
        )
        @NotBlank
        String senha,

        @NotNull
        Date dataNascimento,

        UserRole role

) {
}