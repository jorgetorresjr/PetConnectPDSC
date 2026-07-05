package com.PetConnect.controllers;

import com.PetConnect.DTOs.ServiceDTO;
import com.PetConnect.entities.Service;
import com.PetConnect.entities.User;
import com.PetConnect.repositories.ServiceRepository;
import com.PetConnect.repositories.UserRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/services")
public class ServiceController {

    @Autowired
    private ServiceRepository serviceRepository;

    @Autowired
    private UserRepository userRepository;

    @GetMapping
    public ResponseEntity<List<Service>> listarAtivos() {
        return ResponseEntity.ok(serviceRepository.findByAtivoTrue());
    }

    @GetMapping("/admin/all")
    public ResponseEntity<List<Service>> listarTodos() {
        return ResponseEntity.ok(serviceRepository.findAll());
    }

    @PostMapping("/admin")
    public ResponseEntity<?> cadastrar(@Valid @RequestBody ServiceDTO dto) {
        if (serviceRepository.findByNomeIgnoreCase(dto.nome()).isPresent()) {
            return ResponseEntity.badRequest().body("Já existe um serviço com este nome.");
        }
        Service service = new Service();
        service.setNome(dto.nome());
        service.setDescricao(dto.descricao());
        service.setPrecoBase(dto.precoBase());
        service.setAtivo(true);
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @PatchMapping("/admin/{id}/inativar")
    public ResponseEntity<?> inativar(@PathVariable Long id) {
        Service service = serviceRepository.findById(id).orElse(null);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        service.setAtivo(false);
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @PatchMapping("/admin/{id}/ativar")
    public ResponseEntity<?> ativar(@PathVariable Long id) {
        Service service = serviceRepository.findById(id).orElse(null);
        if (service == null) {
            return ResponseEntity.notFound().build();
        }
        service.setAtivo(true);
        return ResponseEntity.ok(serviceRepository.save(service));
    }

    @PostMapping("/services/{userId}")
    public ResponseEntity<?> selecionarServicos(@PathVariable Long userId, @RequestBody List<Long> servicosIds) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
        List<Service> selectedServices = serviceRepository.findAllById(servicosIds);
        user.setServices(selectedServices);
        userRepository.save(user);
        return ResponseEntity.ok().build();
    }
}
