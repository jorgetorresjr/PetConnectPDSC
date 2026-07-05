package com.PetConnect.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.core.Authentication;
import com.PetConnect.repositories.UserRepository;

import com.PetConnect.DTOs.RegisterDTO;
import com.PetConnect.services.UserService;

import jakarta.validation.Valid;

@RequestMapping("/users")
@RestController
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
        try {
            userService.registerUser(registerDTO);
            return ResponseEntity.ok("Usuário cadastrado com sucesso!");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllUsers() {
        return ResponseEntity.ok(userService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        return userService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping(value = "/{id}/photo", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getUserPhoto(@PathVariable Long id) {
        var userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return ResponseEntity.<byte[]>notFound().build();
        }
        byte[] photo = userOpt.get().getPhoto();
        if (photo == null || photo.length == 0) {
            return ResponseEntity.<byte[]>notFound().build();
        }
        return ResponseEntity.<byte[]>ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(photo);
    }

    @GetMapping("/me")
    public ResponseEntity<?> getMe(Authentication authentication) {
    String email = authentication.getName();
        return userRepository.findByEmail(email)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
}
