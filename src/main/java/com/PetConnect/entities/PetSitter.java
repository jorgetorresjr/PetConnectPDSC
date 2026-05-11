package com.PetConnect.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Column;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "TB_PET_SITTER")
@DiscriminatorValue("PS")
@Getter @Setter @NoArgsConstructor
public class PetSitter extends User {
 
    
  @Column(name = "ESPECIALIDADE")    
  private String specialty;
  @Column(name = "CERTIFICADOS")   
  private String certificates;
  @Column(name = "DISPONIBILIDADE")   
  private String availability;

  @Column(name = "SERVICOS", columnDefinition = "TEXT")
  private String services; // JSON array de serviços

  @Column(name = "PRECOS_SERVICOS", columnDefinition = "TEXT")
  private String servicePrices; // JSON {servico: preco}

  public String getServices() { return services; }
  public void setServices(String services) { this.services = services; }
  public String getServicePrices() { return servicePrices; }
  public void setServicePrices(String servicePrices) { this.servicePrices = servicePrices; }

  public String getSpecialty() { return specialty; }
  public void setSpecialty(String specialty) { this.specialty = specialty; }

  public String getCertificates() { return certificates; }
  public void setCertificates(String certificates) { this.certificates = certificates; }

  public String getAvailability() { return availability; }
  public void setAvailability(String availability) { this.availability = availability; }

  @Override
  public String getDiscriminator() {
    return "PS";
  }
}