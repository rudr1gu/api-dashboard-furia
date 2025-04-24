package com.conectados.conectados.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.conectados.conectados.domain.model.RedeSocial;
import com.conectados.conectados.domain.repository.RedeSocialRepository;

@Service
public class RedeSocialService {

    @Autowired
    private RedeSocialRepository redeSocialRepository;

    public Optional<List<RedeSocial>> buscarTodasRedesSociais() {
        List<RedeSocial> redesSociais = redeSocialRepository.findAll();
        if (redesSociais.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(redesSociais);
    }

    public Optional<RedeSocial> buscarRedeSocialPorId(Long id) {
        return redeSocialRepository.findById(id);
    }

    public RedeSocial cadastrarRedeSocial(RedeSocial redeSocial) {
        return redeSocialRepository.save(redeSocial);
    }

    public RedeSocial atualizarRedeSocial(RedeSocial redeSocial) {
        return redeSocialRepository.save(redeSocial);
    }

    public void deletarRedeSocial(Long id) {
        redeSocialRepository.deleteById(id);
    }
    
}
