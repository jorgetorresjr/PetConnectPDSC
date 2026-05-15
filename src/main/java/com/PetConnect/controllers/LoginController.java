package com.PetConnect.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PetConnect.DTOs.RegisterDTO;
import com.PetConnect.DTOs.LoginDTO;
import com.PetConnect.DTOs.LoginResponseDTO;
import com.PetConnect.entities.PetOwner;
import com.PetConnect.entities.PetSitter;
import com.PetConnect.entities.ExpiredToken;
import com.PetConnect.entities.User;
import com.PetConnect.entities.enums.UserRole;
import com.PetConnect.repositories.ExpiredTokenRepository;
import com.PetConnect.repositories.UserRepository;
import com.PetConnect.services.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/auth")
@RestController
public class LoginController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	TokenService tokenService;
	@Autowired
	private ExpiredTokenRepository expiredTokenRepository;

	@PostMapping("/login")
	       public ResponseEntity<?> login(@RequestBody @Valid LoginDTO login) {
			try {
				var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.senha());
				var authentication = this.authenticationManager.authenticate(usernamePassword);
				var token = tokenService.generateToken((UserDetails) authentication.getPrincipal());

				return ResponseEntity.ok(new LoginResponseDTO(token));
			} catch (BadCredentialsException e) {
				return ResponseEntity
					.status(HttpStatus.UNAUTHORIZED)
					.body("Email ou senha incorretos");
		}
	       }

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Token não encontrado no cabeçalho."));
		}

		String token = authHeader.substring(7);

		   if (expiredTokenRepository.existsByToken(token)) {
			   return ResponseEntity.ok(Map.of("message", "Sessão já encerrada."));
		   }

		   Instant expiresAtInstant = tokenService.extractExpiration(token);

		   LocalDateTime expiresAt = expiresAtInstant
				   .atZone(ZoneOffset.of("-03:00"))
				   .toLocalDateTime();

		   expiredTokenRepository.save(new ExpiredToken(token, expiresAt));

		   return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso."));
	}

		@PostMapping("/register")
		public ResponseEntity<?> register(@RequestBody @Valid RegisterDTO registerDTO) {
			       if (userRepository.findByEmail(registerDTO.email()).isPresent()) {
				       return ResponseEntity.badRequest().body("Email already registered");
			       }

			       String encryptedPassword = new BCryptPasswordEncoder().encode(registerDTO.password());
			       User newUser;
			       if ("PO".equals(registerDTO.role())) {
				       newUser = new PetOwner();
			       } else if ("PS".equals(registerDTO.role())) {
				       newUser = new PetSitter();
			       } else {
				       return ResponseEntity.badRequest().body("Invalid role.");
			       }
			       newUser.setName(registerDTO.name());
			       newUser.setEmail(registerDTO.email());
			       newUser.setPassword(encryptedPassword);
			       newUser.setCpf(registerDTO.cpf());
			       newUser.setLogin(registerDTO.email() != null ? registerDTO.email() : registerDTO.email());
			       newUser.setAddress(registerDTO.address());
			       newUser.setPhone(registerDTO.phone());
			       newUser.setBirthDate(registerDTO.birthDate());
			       userRepository.save(newUser);
			return ResponseEntity.ok().build();
	}
}