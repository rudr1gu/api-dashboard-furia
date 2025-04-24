package com.conectados.conectados.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.conectados.conectados.domain.model.RedeSocial;

public interface RedeSocialRepository extends JpaRepository<RedeSocial, Long> {
    // Custom query methods can be defined here if needed

}
