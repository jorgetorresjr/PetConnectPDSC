package com.PetConnect.controllers;

import com.PetConnect.entities.DTOs.CadastroDTO;
import com.PetConnect.entities.DTOs.LoginDTO;
import com.PetConnect.entities.DTOs.LoginResponseDTO;
import com.PetConnect.entities.Usuario;
import com.PetConnect.repositories.UsuarioRepository;
import com.PetConnect.services.TokenService;
import jakarta.validation.Valid;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/auth")
@RestController
public class LoginController {
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private UsuarioRepository userRepository;
    @Autowired
    TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid LoginDTO login) {
        var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.senha());
        var authentication = this.authenticationManager.authenticate(usernamePassword);

        var token = tokenService.generateToken((UserDetails) authentication.getPrincipal());

        return ResponseEntity.ok(new LoginResponseDTO(token));
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid CadastroDTO cadastro) {

        if (userRepository.findByEmail(cadastro.email()) != null) {
            return ResponseEntity.badRequest().body("Email já cadastrado");
        }

        String encryptedPassword = new BCryptPasswordEncoder().encode(cadastro.senha());

//        PetOwner user = new PetOwner();
//
//        user.setNome(cadastro.nome());
//        user.setEmail(cadastro.email());
//        user.setSenha(encryptedPassword);
//        user.setCpf(cadastro.cpf());
//        user.setLogin(cadastro.login());
//        user.setEndereco(cadastro.endereco());
//        user.setTelefones(cadastro.telefones());
//        user.setDataNascimento(cadastro.dataNascimento());
//
//        userRepository.save(user);

        return ResponseEntity.ok().build();
    }
}

