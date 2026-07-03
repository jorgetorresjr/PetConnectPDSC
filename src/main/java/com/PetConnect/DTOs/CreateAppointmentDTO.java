package com.PetConnect.DTOs;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class CreateAppointmentDTO {

	private Long petSitterId;
    private Long petId;
    private Long serviceId;
    private String serviceDate;
    private String serviceTime;
    private String status;

    // Construtor vazio (obrigatório para o Spring)
    public CreateAppointmentDTO() {}

    @JsonCreator
    public CreateAppointmentDTO(
            @JsonProperty("petSitterId") Long petSitterId,
            @JsonProperty("petId") Long petId,
            @JsonProperty("serviceId") Long serviceId,
            @JsonProperty("serviceDate") String serviceDate,
            @JsonProperty("serviceTime") String serviceTime,
            @JsonProperty("status") String status) {
        this.petSitterId = petSitterId;
        this.petId = petId;
        this.serviceId = serviceId;
        this.serviceDate = serviceDate;
        this.serviceTime = serviceTime;
        this.status = status;
    }
    
    // Getters e Setters (Essenciais para o Spring "encher" a classe com os dados)
    public Long getPetSitterId() { return petSitterId; }
    public void setPetSitterId(Long petSitterId) { this.petSitterId = petSitterId; }

    public Long getPetId() { return petId; }
    public void setPetId(Long petId) { this.petId = petId; }

    public Long getServiceId() { return serviceId; }
    public void setServiceId(Long serviceId) { this.serviceId = serviceId; }

    public String getServiceDate() { return serviceDate; }
    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }

    public String getServiceTime() { return serviceTime; }
    public void setServiceTime(String serviceTime) { this.serviceTime = serviceTime; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
