package com.PetConnect.DTOs;

import com.PetConnect.entities.Address;
import com.PetConnect.entities.enums.UserRole;
import com.PetConnect.validators.Adulthood;
import com.fasterxml.jackson.annotation.JsonFormat;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import org.hibernate.validator.constraints.br.CPF;

import java.util.Date;

public record RegisterDTO(

    @Valid
    @NotNull(message = "O endereço é obrigatório.")
    Address address,

    @NotBlank(message = "O telefone é obrigatório.")
    @Size(min = 10, max = 15, message = "O telefone deve ter entre 10 e 15 caracteres.")
    String phone,

    @CPF(message = "CPF inválido.")
    @NotBlank(message = "O CPF é obrigatório.")
    String cpf,

    @NotBlank(message = "O nome é obrigatório.")
    @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres.")
    @Pattern(
            regexp = "^[A-Za-zÀ-ÿ' -]+$",
            message = "O nome deve conter apenas letras, espaços, hífen ou apóstrofo."
    )
    String name,

    @Email(message = "E-mail inválido.")
    @NotBlank(message = "O e-mail é obrigatório.")
    @Size(max = 100, message = "O e-mail deve ter no máximo 100 caracteres.")
    String email,

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 8, max = 60, message = "A senha deve ter no mínimo 8 caracteres.")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*\\W).+$",
            message = "A senha deve conter maiúscula, minúscula, número e caractere especial."
    )
    String password,

    @NotNull(message = "A data de nascimento é obrigatória.")
    @Past(message = "A data de nascimento deve ser anterior à data atual.")
    @Adulthood(message = "O usuário deve ter entre 18 e 110 anos.")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    Date birthDate,

    String role

) {}