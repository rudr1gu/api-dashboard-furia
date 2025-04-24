package com.conectados.conectados.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.conectados.conectados.domain.model.Evento;
import com.conectados.conectados.services.EventoService;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/eventos")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class EventoController {

    @Autowired
    private EventoService eventoService;

    @GetMapping("/all")
    public ResponseEntity<List<Evento>> getAllEventos() {
        return ResponseEntity.ok(eventoService.buscarTodosEventos().orElse(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Evento> getById(@PathVariable Long id) {
        return eventoService.buscarEventoPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<Evento> cadastrarEvento(@RequestBody Evento evento) {
        return ResponseEntity.ok(eventoService.cadastrarEvento(evento));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<Evento> atualizarEvento(@PathVariable Long id, @RequestBody Evento evento) {
        evento.setId(id);
        return ResponseEntity.ok(eventoService.atualizarEvento(evento));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletarEvento(@PathVariable Long id) {
        if (eventoService.buscarEventoPorId(id).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Evento não encontrado");
        }
        eventoService.DeletarEvento(id);
    }

}