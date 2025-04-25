package com.conectados.conectados.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conectados.conectados.domain.model.Interacao;
import com.conectados.conectados.domain.repository.InteracaoRepository;

@Service
public class InteracaoService {

    @Autowired
    private InteracaoRepository interacaoRepository;

    public Optional<List<Interacao>> buscarTodasInteracoes() {
        List<Interacao> interacoes = interacaoRepository.findAll();
        if (interacoes.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(interacoes);
    }

    public Optional<Interacao> buscarInteracaoPorId(Long id) {
        return interacaoRepository.findById(id);
    }

    public Interacao cadastrarInteracao(Interacao interacao) {
        return interacaoRepository.save(interacao);
    }

    public Interacao atualizarInteracao(Interacao interacao) {
        return interacaoRepository.save(interacao);
    }

    public void deletarInteracao(Long id) {
        Optional<Interacao> interacao = buscarInteracaoPorId(id);
        if (interacao.isEmpty()) {
            throw new RuntimeException("Interação não encontrada");
        }
        interacaoRepository.deleteById(id);
    }

}
