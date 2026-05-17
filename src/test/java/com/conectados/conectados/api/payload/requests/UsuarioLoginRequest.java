package com.conectados.conectados.api.payload.requests;

public record UsuarioLoginRequest(
        String email,
        String senha
) {
}
