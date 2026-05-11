package com.PetConnect.DTOs;

import com.PetConnect.entities.Address;
import com.PetConnect.entities.enums.UserRole;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.br.CPF;

import java.util.Date;

public record RegisterDTO(
        @Valid
        @NotNull
        Address address,

        String phone,

        @CPF
        @NotBlank
        String cpf,



        @NotBlank
        @Pattern(
                regexp = "^[A-ZÀ-Ú][a-zà-ú]+(?:\\s[A-ZÀ-Ú][a-zà-ú]+)*$",
                message = "Invalid name"
        )
        String name,

        @Email
        @NotBlank
        String email,

        @Pattern(
                regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
                message = "Password must contain uppercase, lowercase, number and special character"
        )
        @NotBlank
        String password,

        @NotNull
        Date birthDate,

        String role

) {}