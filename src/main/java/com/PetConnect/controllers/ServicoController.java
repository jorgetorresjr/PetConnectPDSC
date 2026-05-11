package com.PetConnect.controllers;

import com.PetConnect.entities.Servico;
import com.PetConnect.entities.Usuario;
import com.PetConnect.repositories.ServicoRepository;
import com.PetConnect.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/servicos")
public class ServicoController {

    @Autowired
    private ServicoRepository servicoRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @GetMapping
    public ResponseEntity<List<Servico>> listarTodos() {
        return ResponseEntity.ok(servicoRepository.findAll());
    }

    @PostMapping("/selecionar/{usuarioId}")
    public ResponseEntity<?> selecionarServicos(@PathVariable Long usuarioId, @RequestBody List<Long> servicosIds) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        List<Servico> servicosSelecionados = servicoRepository.findAllById(servicosIds);
        usuario.setServicos(servicosSelecionados);
        
        usuarioRepository.save(usuario);
        return ResponseEntity.ok().build();
    }
}