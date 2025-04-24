package com.conectados.conectados.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.Nivel;

public interface NivelRepository extends JpaRepository<Nivel, Long> {
    // Custom query methods can be defined here if needed
    
}
