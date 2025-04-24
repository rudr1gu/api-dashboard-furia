package com.conectados.conectados.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.conectados.conectados.services.InteracaoService;

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
@RequestMapping("/interacoes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class InteracaoController {

    @Autowired
    private InteracaoService interacaoService;

    @GetMapping("/all")
    public ResponseEntity<List<com.conectados.conectados.domain.model.Interacao>> getAllInteracoes() {
        return ResponseEntity.ok(interacaoService.buscarTodasInteracoes().orElse(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<com.conectados.conectados.domain.model.Interacao> getById(@PathVariable Long id) {
        return interacaoService.buscarInteracaoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<com.conectados.conectados.domain.model.Interacao> cadastrarInteracao(@RequestBody com.conectados.conectados.domain.model.Interacao interacao) {
        return ResponseEntity.ok(interacaoService.cadastrarInteracao(interacao));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<com.conectados.conectados.domain.model.Interacao> atualizarInteracao(@PathVariable Long id, @RequestBody com.conectados.conectados.domain.model.Interacao interacao) {
        interacao.setId(id);
        return ResponseEntity.ok(interacaoService.atualizarInteracao(interacao));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletarInteracao(@PathVariable Long id) {
        if (interacaoService.buscarInteracaoPorId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Interação não encontrada");
        }
        interacaoService.deletarInteracao(id);
    }
    
}
