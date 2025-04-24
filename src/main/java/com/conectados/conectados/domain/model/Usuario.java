package com.conectados.conectados.domain.model;

import java.time.LocalDate;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "tb_usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String nickName;

    @NotBlank
    @Email
    @Column(unique = true)
    private String email;

    @NotBlank
    private String senha;

    @NotBlank
    private String tipo;

    private String avatar;

    @CreationTimestamp
    private LocalDate dataCriacao;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({ "usuario", "respostas" })
    private List<Postagem> postagens;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    @JsonIgnoreProperties({ "usuario", "postagem" })
    private List<Resposta> respostas;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL)
    private List<RedeSocial> redesSociais;

    @ManyToOne
    @JsonIgnoreProperties("usuarios")
    private Nivel nivel;

    @ManyToMany
    @JoinTable(name = "usuario_jogo", joinColumns = @JoinColumn(name = "usuario_id"), inverseJoinColumns = @JoinColumn(name = "jogo_id"))
    @JsonIgnoreProperties("usuarios")
    private List<Jogo> jogos;
}