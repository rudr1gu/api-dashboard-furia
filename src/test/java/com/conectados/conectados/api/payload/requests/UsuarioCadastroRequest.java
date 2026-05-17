package com.conectados.conectados.api.payload.requests;

public record UsuarioCadastroRequest(
        String nickName,
        String email,
        String senha,
        String tipo,
        String avatar,
        String bio
) {
}
