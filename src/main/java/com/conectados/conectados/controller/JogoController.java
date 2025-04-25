package com.conectados.conectados.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.conectados.conectados.domain.model.Jogo;
import com.conectados.conectados.services.JogoService;

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
@RequestMapping("/jogos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class JogoController {

    @Autowired
    private JogoService jogoService;

    @GetMapping("/all")
    public ResponseEntity<List<Jogo>> getAllJogos() {
        return ResponseEntity.ok(jogoService.buscarTodosJogos().orElse(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Jogo> getById(@PathVariable Long id) {
        return jogoService.buscarJogoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Jogo> cadastrarJogo(@RequestBody Jogo jogo) {
        return ResponseEntity.ok(jogoService.cadastrarJogo(jogo));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Jogo> atualizarJogo(@PathVariable Long id, @RequestBody Jogo jogo) {
        jogo.setId(id);
        return ResponseEntity.ok(jogoService.atualizarJogo(jogo));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletarJogo(@PathVariable Long id) {
        jogoService.deletarJogo(id);
    }
}
