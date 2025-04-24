package com.conectados.conectados.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conectados.conectados.domain.model.Evento;
import com.conectados.conectados.domain.repository.EventoRepository;

@Service
public class EventoService {

    @Autowired
    private EventoRepository eventoRepository;

    public Optional<Evento> buscarEventoPorId(Long id) {
        return eventoRepository.findById(id);
    }

    public Evento cadastrarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public Optional<List<Evento>> buscarTodosEventos() {
        List<Evento> eventos = eventoRepository.findAll();
        if (eventos.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(eventos);
    }

    public Evento atualizarEvento(Evento evento) {
        return eventoRepository.save(evento);
    }

    public void DeletarEvento(Long id) {
        eventoRepository.deleteById(id);
    }

}
