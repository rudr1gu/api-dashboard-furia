package com.conectados.conectados.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.Interacao;

public interface InteracaoRepository extends JpaRepository<Interacao, Long> {
    // Custom query methods can be defined here if needed

}
