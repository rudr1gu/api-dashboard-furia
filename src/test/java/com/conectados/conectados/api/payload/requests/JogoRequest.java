package com.conectados.conectados.api.payload.requests;

public record JogoRequest(
        String nome,
        String imagemUrl,
        String descricao
) {
}
