package com.conectados.conectados.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conectados.conectados.domain.model.Nivel;
import com.conectados.conectados.domain.repository.NivelRepository;


@Service
public class NivelService {

    @Autowired
    private NivelRepository nivelRepository;

    public Optional<List<Nivel>> buscarTodosNiveis() {
        List<Nivel> niveis = nivelRepository.findAll();
        if (niveis.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(niveis);
    }

    public Optional<Nivel> buscarNivelPorId(Long id) {
        return nivelRepository.findById(id);
    }

    public Nivel cadastrarNivel(Nivel nivel) {
        return nivelRepository.save(nivel);
    }

    public Nivel atualizarNivel(Nivel nivel) {
        return nivelRepository.save(nivel);
    }

    public void deletarNivel(Long id) {
        nivelRepository.deleteById(id);
    }

}
