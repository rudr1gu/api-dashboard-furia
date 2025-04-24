package com.conectados.conectados.domain.model;

import java.time.LocalDate;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

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
    @JsonIgnoreProperties({ "postagens", "respostas", "nivel", "redesSociais", "jogos" })
    private Usuario usuario;

    @ManyToOne
    @JsonIgnoreProperties({ "usuario", "respostas" })
    private Postagem postagem;
}
