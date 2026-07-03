package com.PetConnect.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_PET")
@Getter
@Setter
@NoArgsConstructor
public class Pet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do pet é obrigatório.")
    @Size(min = 2, max = 100, message = "O nome deve ter entre 2 e 100 caracteres.")
    @Column(name = "NOME", nullable = false)
    private String name;

    @NotBlank(message = "A espécie é obrigatória.")
    @Size(min = 2, max = 50, message = "A espécie deve ter entre 2 e 50 caracteres.")
    @Column(name = "ESPECIE", nullable = false)
    private String specie;

    @NotBlank(message = "A raça é obrigatória.")
    @Size(min = 2, max = 50, message = "A raça deve ter entre 2 e 50 caracteres.")
    @Column(name = "RACA", nullable = false)
    private String breed;

    @NotNull(message = "A idade é obrigatória.")
    @Positive(message = "A idade deve ser um número positivo.")
    @Column(name = "IDADE", nullable = false)
    private Integer age;

    @Size(max = 500, message = "As observações devem ter no máximo 500 caracteres.")
    @Column(name = "OBSERVACOES")
    private String observations;

    @ManyToOne
    @JoinColumn(name = "owner_id")
    private PetOwner owner;

    @Lob
    @Column(name = "IMG_FOTO", nullable = true, columnDefinition = "BLOB")
    private byte[] photo;

    // Getters e setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String nome) {
        this.name = nome;
    }

    public String getSpecie() {
        return specie;
    }

    public void setSpecie(String specie) {
        this.specie = specie;
    }

    public String getBreed() {
        return breed;
    }

    public void setBreed(String breed) {
        this.breed = breed;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public PetOwner getOwner() {
        return owner;
    }

    public void setOwner(PetOwner owner) {
        this.owner = owner;
    }

    public byte[] getPhoto() {
        return photo;
    }

    public void setPhoto(byte[] photo) {
        this.photo = photo;
    }
}