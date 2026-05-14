package com.PetConnect.controllers;

import com.PetConnect.entities.Service;
import com.PetConnect.entities.User;
import com.PetConnect.repositories.ServiceRepository;
import com.PetConnect.repositories.UserRepository;
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
    public ResponseEntity<List<Service>> listarTodos() {
        return ResponseEntity.ok(serviceRepository.findAll());
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