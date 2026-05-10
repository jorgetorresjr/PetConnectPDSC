package com.PetConnect.controllers;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.PetConnect.entities.PetOwner;
import com.PetConnect.entities.PetSitter;
import com.PetConnect.entities.TokenExpirado;
import com.PetConnect.entities.Usuario;
import com.PetConnect.entities.DTOs.CadastroDTO;
import com.PetConnect.entities.DTOs.LoginDTO;
import com.PetConnect.entities.DTOs.LoginResponseDTO;
import com.PetConnect.entities.enums.UserRole;
import com.PetConnect.repositories.TokenExpiradoRepository;
import com.PetConnect.repositories.UsuarioRepository;
import com.PetConnect.services.TokenService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

@RequestMapping("/auth")
@RestController
public class LoginController {
	@Autowired
	private AuthenticationManager authenticationManager;
	@Autowired
	private UsuarioRepository userRepository;
	@Autowired
	TokenService tokenService;
	@Autowired
	private TokenExpiradoRepository tokenExpiradoRepository;

	@PostMapping("/login")
	public ResponseEntity login(@RequestBody @Valid LoginDTO login) {
		var usernamePassword = new UsernamePasswordAuthenticationToken(login.email(), login.senha());
		var authentication = this.authenticationManager.authenticate(usernamePassword);

		var token = tokenService.generateToken((UserDetails) authentication.getPrincipal());

		return ResponseEntity.ok(new LoginResponseDTO(token));
	}

	@PostMapping("/logout")
	public ResponseEntity<Map<String, String>> logout(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return ResponseEntity.badRequest()
					.body(Map.of("error", "Token não encontrado no cabeçalho."));
		}

		String token = authHeader.substring(7);

		if (tokenExpiradoRepository.existsByToken(token)) {
			return ResponseEntity.ok(Map.of("message", "Sessão já encerrada."));
		}

		Instant expiresAtInstant = tokenService.extractExpiration(token);

		LocalDateTime expiresAt = expiresAtInstant
				.atZone(ZoneOffset.of("-03:00"))
				.toLocalDateTime();

		tokenExpiradoRepository.save(new TokenExpirado(token, expiresAt));

		return ResponseEntity.ok(Map.of("message", "Logout realizado com sucesso."));
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody @Valid CadastroDTO cadastro) {

		if (userRepository.findByEmail(cadastro.email()).isPresent()) {
			return ResponseEntity.badRequest().body("Email já cadastrado");
		}

		String encryptedPassword = new BCryptPasswordEncoder().encode(cadastro.senha());

		Usuario novoUsuario;

		// Regra da US05: Criar Perfil de Tutor (PetOwner)
		if (cadastro.role() == UserRole.PET_OWNER) {
			novoUsuario = new PetOwner();
		} else if (cadastro.role() == UserRole.PET_SITTER) {
			novoUsuario = new PetSitter();
		} else {
			return ResponseEntity.badRequest().body("Role inválida.");
		}

		novoUsuario.setNome(cadastro.nome());
		novoUsuario.setEmail(cadastro.email());
		novoUsuario.setSenha(encryptedPassword);
		novoUsuario.setCpf(cadastro.cpf());
		novoUsuario.setLogin(cadastro.login()!= null ? cadastro.login() : cadastro.email()); // usando email como login
		novoUsuario.setEndereco(cadastro.endereco());
		novoUsuario.setTelefone(cadastro.telefone());
		novoUsuario.setDataNascimento(cadastro.dataNascimento());

		userRepository.save(novoUsuario);

		return ResponseEntity.ok().build();
	}
}

