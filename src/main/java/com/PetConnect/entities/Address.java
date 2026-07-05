package com.PetConnect.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class Address {

@NotBlank(message = "O logradouro é obrigatório.")
@Size(max = 150, message = "O logradouro deve ter no máximo 150 caracteres.")
@Column(name = "END_TXT_LOGRADOURO", length = 150, nullable = false)
private String street;

@NotBlank(message = "O bairro é obrigatório.")
@Size(max = 150, message = "O bairro deve ter no máximo 150 caracteres.")
@Column(name = "END_TXT_BAIRRO", length = 150, nullable = false)
private String neighborhood;

@NotNull(message = "O número é obrigatório.")
@Positive(message = "O número deve ser positivo.")
@Column(name = "END_NUMERO", nullable = false)
private Integer number;

@Size(max = 30, message = "O complemento deve ter no máximo 30 caracteres.")
@Column(name = "END_TXT_COMPLEMENTO", length = 30)
private String complement;

@NotBlank(message = "O CEP é obrigatório.")
@Pattern(
        regexp = "^\\d{5}-\\d{3}$",
        message = "O CEP deve conter XXXXX-XXX números."
)
@Column(name = "END_TXT_CEP", length = 9, nullable = false)
private String cep;

@NotBlank(message = "A cidade é obrigatória.")
@Size(min = 2, max = 50, message = "A cidade deve ter entre 2 e 50 caracteres.")
@Column(name = "END_TXT_CIDADE", length = 50, nullable = false)
private String city;

@NotBlank(message = "O estado é obrigatório.")
@Size(min = 2, max = 2, message = "O estado deve ter exatamente 2 caracteres (ex: SP, RJ).")
@Column(name = "END_TXT_ESTADO", length = 2, nullable = false)
private String state;
}