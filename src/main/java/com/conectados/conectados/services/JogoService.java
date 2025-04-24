package com.conectados.conectados.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conectados.conectados.domain.model.Jogo;
import com.conectados.conectados.domain.repository.JogoRepository;

@Service
public class JogoService {

    @Autowired
    private JogoRepository jogoRepository;

    public Optional<List<Jogo>> buscarTodosJogos() {
        List<Jogo> jogos = jogoRepository.findAll();
        if (jogos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(jogos);
    }

    public Optional<Jogo> buscarJogoPorId(Long id) {
        return jogoRepository.findById(id);
    }

    public Jogo cadastrarJogo(Jogo jogo) {
        return jogoRepository.save(jogo);
    }

    public Jogo atualizarJogo(Jogo jogo) {
        return jogoRepository.save(jogo);
    }

    public void deletarJogo(Long id) {
        jogoRepository.deleteById(id);
    }

}
