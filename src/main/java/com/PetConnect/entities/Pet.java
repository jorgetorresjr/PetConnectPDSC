package com.PetConnect.entities;

import jakarta.persistence.*;
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

	@Column(name = "NOME", nullable = false)
	private String name;
	@Column(name = "ESPECIE", nullable = false)
	private String specie;
	@Column(name = "RACA", nullable = false)
	private String breed;
	@Column(name = "IDADE", nullable = false)
	private Integer age;
	@Column(name = "OBSERVACOES")
	private String observations;

	// Relacionamento: Muitos pets para um dono (PetOwner)
	@ManyToOne
	@JoinColumn(name = "owner_id")
	private PetOwner owner;

	// getters e setters

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

	@Lob
	@Column(name = "IMG_FOTO", nullable = true, columnDefinition = "BLOB")
	private byte[] photo;

	public byte[] getPhoto() {
		return photo;
	}

	public void setPhoto(byte[] photo) {
		this.photo = photo;
	}

}