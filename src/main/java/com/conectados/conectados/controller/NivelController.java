package com.conectados.conectados.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.conectados.conectados.domain.model.Nivel;
import com.conectados.conectados.services.NivelService;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/niveis")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class NivelController{

    @Autowired
    private NivelService nivelService;

    @GetMapping("/all")
    public ResponseEntity<List<Nivel>> getAllNiveis() {
        return ResponseEntity.ok(nivelService.buscarTodosNiveis().orElse(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Nivel> getById(@PathVariable Long id) {
        return nivelService.buscarNivelPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Nivel> cadastrarNivel(@RequestBody Nivel nivel) {
        return ResponseEntity.ok(nivelService.cadastrarNivel(nivel));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Nivel> atualizarNivel(@PathVariable Long id, @RequestBody Nivel nivel) {
        nivel.setId(id);
        return ResponseEntity.ok(nivelService.atualizarNivel(nivel));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletarNivel(@PathVariable Long id) {
        if (nivelService.buscarNivelPorId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Nível não encontrado");
        }
        nivelService.deletarNivel(id);
    }
}
