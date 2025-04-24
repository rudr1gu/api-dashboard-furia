package com.conectados.conectados.domain.model;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_respostas")
public class Resposta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String conteudo;

    @CreationTimestamp
    private LocalDate dataCriacao;

    @CreationTimestamp
    private LocalDate dataAtualizacao;

    @ManyToOne
    private Usuario usuario;

    @ManyToOne
    private Postagem postagem;
}
