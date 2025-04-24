package com.conectados.conectados.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.Jogo;

public interface JogoRepository extends JpaRepository<Jogo, Long> {
    // Custom query methods can be defined here if needed

}
