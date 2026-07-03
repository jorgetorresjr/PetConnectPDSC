package com.PetConnect.controllers;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.PetConnect.DTOs.PetOwnerDTO;
import com.PetConnect.entities.PetOwner;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.UserRepository;

@RestController
@RequestMapping("/petowners")
public class PetOwnerController {
    @Autowired
    private PetOwnerRepository petOwnerRepository;
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/profile")
    public ResponseEntity<PetOwnerDTO> updateProfile(

            @RequestParam(required = false) String phone,
            @RequestParam(required = false) MultipartFile photo

    ) throws IOException {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        var user = userOpt.get();
        PetOwner petOwner;
        if (user instanceof PetOwner) {
            petOwner = (PetOwner) user;
        } else {

            petOwner = new PetOwner();

            petOwner.setId(user.getId());
            petOwner.setName(user.getName());
            petOwner.setEmail(user.getEmail());
            petOwner.setPhone(user.getPhone());
            petOwner.setCpf(user.getCpf());
            petOwner.setLogin(user.getLogin());
            petOwner.setPassword(user.getPassword());
            petOwner.setBirthDate(user.getBirthDate());
            petOwner.setAddress(user.getAddress());
            petOwner.setPhoto(user.getPhoto());

        }

        if (phone != null && !phone.isBlank()) {
            petOwner.setPhone(phone);
        }

        if (photo != null && !photo.isEmpty()) {
            petOwner.setPhoto(photo.getBytes());
        }

        petOwnerRepository.save(petOwner);

        return ResponseEntity.ok(new PetOwnerDTO(petOwner));

    }
    
    @GetMapping("/me")
    public ResponseEntity<PetOwnerDTO> getMyProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        
        PetOwner petOwner = petOwnerRepository.findByEmail(email).orElse(null);
        
        if (petOwner == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(new PetOwnerDTO(petOwner));
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<PetOwner> getById(@PathVariable Long id) {
        return petOwnerRepository.findById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}