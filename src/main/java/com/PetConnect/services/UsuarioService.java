package com.PetConnect.services;

import com.PetConnect.entities.Usuario;
import com.PetConnect.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private UsuarioRepository usuarioRepository;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public Usuario createUsuario(Usuario Usuario) {
        String encodedPassword = passwordEncoder.encode(Usuario.getSenha());
        Usuario.setSenha(encodedPassword);

        return usuarioRepository.save(Usuario);
    }

    public Usuario updateUsuario(Long id, Usuario newUsuario) {
        Usuario oldUsuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado. ID: " + id));

        oldUsuario.setNome(newUsuario.getNome());
        oldUsuario.setTelefone(newUsuario.getTelefone());
        oldUsuario.setEndereco(newUsuario.getEndereco());
        oldUsuario.setEmail(newUsuario.getEmail());
        oldUsuario.setDataNascimento(newUsuario.getDataNascimento());
        oldUsuario.setFoto(newUsuario.getFoto());

        return usuarioRepository.save(oldUsuario);
    }

    public void changePassword(Long id, String oldPassword, String newPassword) {
        Usuario oldUsuario = usuarioRepository.findById(id).get();

        if (!passwordEncoder.matches(oldPassword, oldUsuario.getSenha())) {
            throw new RuntimeException("Senha errada");
        }

        oldUsuario.setSenha(passwordEncoder.encode(newPassword));
        usuarioRepository.save(oldUsuario);
    }

    public void deleteUsuario(Long id) {
        usuarioRepository.deleteById(id);
    }

    public Optional<Usuario> findById(Long id) {
        return usuarioRepository.findById(id);
    }

    public Usuario findByEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));
    }
    public List<Usuario> findAll() {
        return usuarioRepository.findAll();
    }
}