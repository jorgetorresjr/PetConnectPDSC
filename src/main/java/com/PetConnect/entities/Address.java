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

    @NotBlank
    @Size(max = 150)
    @Column(name = "END_TXT_LOGRADOURO", length = 150, nullable = false)
    private String street;

    @NotBlank
    @Size(max = 150)
    @Column(name = "END_TXT_BAIRRO", length = 150, nullable = false)
    private String neighborhood;

    @NotNull
    @Positive
    @Column(name = "END_NUMERO", nullable = false)
    private Integer number;

    @Size(max = 30)
    @Column(name = "END_TXT_COMPLEMENTO", length = 30)
    private String complement;

    @NotBlank
    @Pattern(
            regexp = "^\\d{5}-\\d{3}$",
            message = "{exemplo.jpa.Endereco.cep}"
    )
    @Column(name = "END_TXT_CEP", length = 9, nullable = false)
    private String cep;

    @NotBlank
    @Size(min = 2, max = 50)
    @Column(name = "END_TXT_CIDADE", length = 50, nullable = false)
    private String city;

    @NotBlank
    @Size(min = 2, max = 2)
    @Column(name = "END_TXT_ESTADO", length = 2, nullable = false)
    private String state;
}