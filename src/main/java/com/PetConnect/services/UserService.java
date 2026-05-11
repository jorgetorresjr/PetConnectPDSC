package com.PetConnect.services;

import com.PetConnect.DTOs.RegisterDTO;
import com.PetConnect.entities.PetOwner;
import com.PetConnect.entities.PetSitter;
import com.PetConnect.entities.User;
import com.PetConnect.entities.enums.UserRole;
import com.PetConnect.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public void registerUser(RegisterDTO registerDTO) {
        if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado");
        }
        String encryptedPassword = passwordEncoder.encode(registerDTO.password());
        User newUser;
        String role = (registerDTO.role() != null) ? registerDTO.role().toString() : null;
        if ("PO".equals(role)) {
            newUser = new PetOwner();
        } else if ("PS".equals(role)) {
            newUser = new PetSitter();
        } else {
            throw new IllegalArgumentException("Role inválida.");
        }
        newUser.setName(registerDTO.name());
        newUser.setEmail(registerDTO.email());
        newUser.setPassword(encryptedPassword);
        newUser.setCpf(registerDTO.cpf());
        newUser.setLogin(registerDTO.email()); // login sempre igual ao email
        newUser.setAddress(registerDTO.address());
        newUser.setPhone(registerDTO.phone());
        newUser.setBirthDate(registerDTO.birthDate());
        userRepository.save(newUser);
    }
    public User createUser(User user) {
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        return userRepository.save(user);
    }

    public User updateUser(Long id, User newUser) {
        User oldUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found. ID: " + id));

        oldUser.setName(newUser.getName());
        oldUser.setPhone(newUser.getPhone());
        oldUser.setAddress(newUser.getAddress());
        oldUser.setEmail(newUser.getEmail());
        oldUser.setBirthDate(newUser.getBirthDate());
        oldUser.setPhoto(newUser.getPhoto());

        return userRepository.save(oldUser);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        User oldUser = userRepository.findById(id).get();

        if (!passwordEncoder.matches(oldPassword, oldUser.getPassword())) {
            throw new RuntimeException("Wrong password");
        }

        oldUser.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(oldUser);
    }

    public void deleteUser(Long id) {
        userRepository.deleteById(id);
    }

    public Optional<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public User findByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }
}