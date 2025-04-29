package com.conectados.conectados.domain.repository;

import java.util.List;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.Postagem;

public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    
    @EntityGraph(attributePaths = { "usuario", "respostas", "respostas.usuario" })
    List<Postagem> findAll();
}
