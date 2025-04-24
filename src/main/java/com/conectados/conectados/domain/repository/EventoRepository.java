package com.conectados.conectados.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.Evento;

public interface EventoRepository extends JpaRepository<Evento, Long> {
    // Custom query methods can be defined here if needed

}
