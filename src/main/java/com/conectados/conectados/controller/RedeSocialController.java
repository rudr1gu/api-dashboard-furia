package com.conectados.conectados.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.conectados.conectados.domain.model.RedeSocial;
import com.conectados.conectados.services.RedeSocialService;

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
@RequestMapping("/redes-sociais")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RedeSocialController {

    @Autowired
    private RedeSocialService redeSocialService;

    @GetMapping("/all")
    public ResponseEntity<List<RedeSocial>> getAllRedesSociais() {
        return ResponseEntity.ok(redeSocialService.buscarTodasRedesSociais().orElse(null));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RedeSocial> getById(@PathVariable Long id) {
        return redeSocialService.buscarRedeSocialPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<RedeSocial> cadastrarRedeSocial(@RequestBody RedeSocial redeSocial) {
        return ResponseEntity.ok(redeSocialService.cadastrarRedeSocial(redeSocial));
    }

    @PutMapping("/atualizar/{id}")
    public ResponseEntity<RedeSocial> atualizarRedeSocial(@PathVariable Long id, @RequestBody RedeSocial redeSocial) {
        redeSocial.setId(id);
        return ResponseEntity.ok(redeSocialService.atualizarRedeSocial(redeSocial));
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @DeleteMapping("/{id}")
    public void deletarRedeSocial(@PathVariable Long id) {
        redeSocialService.deletarRedeSocial(id);
    }
}
