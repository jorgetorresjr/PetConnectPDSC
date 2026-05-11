package com.PetConnect.controllers;

import com.PetConnect.entities.PetOwner;
import com.PetConnect.repositories.PetOwnerRepository;
import com.PetConnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/petowners")
public class PetOwnerController {
    @Autowired
    private PetOwnerRepository petOwnerRepository;
    @Autowired
    private UserRepository userRepository;

    @PutMapping("/profile")
    public ResponseEntity<PetOwner> updateProfile(
            @RequestParam(required = false) MultipartFile photo
    ) throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        var userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().build();
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
        if (photo != null && !photo.isEmpty()) {
            petOwner.setPhoto(photo.getBytes());
        }
        PetOwner saved = petOwnerRepository.save(petOwner);
        return ResponseEntity.ok(saved);
    }
}