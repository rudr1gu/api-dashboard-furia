package com.conectados.conectados.api.tests;

import com.conectados.conectados.api.config.ApiTestConfig;
import com.conectados.conectados.api.payload.requests.JogoRequest;
import com.conectados.conectados.api.payload.requests.UsuarioCadastroRequest;
import com.conectados.conectados.api.payload.requests.UsuarioLoginRequest;
import com.conectados.conectados.api.utils.EvidenceUtils;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class JogoApiTest extends ApiTestConfig {

    @Test
    void deveCadastrarUsuarioComSucesso() {
        UsuarioCadastroRequest request = novoUsuario("cadastro");

        Response response = usuarioClient.cadastrar(request);
        EvidenceUtils.save("usuario-cadastrar", "POST", "/usuarios/cadastrar", request, response);

        response.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .header("Content-Type", containsString("application/json"))
                .body("id", notNullValue())
                .body("nickName", equalTo(request.nickName()))
                .body("email", equalTo(request.email()))
                .body("tipo", equalTo(request.tipo()));
    }

    @Test
    void deveExecutarFluxoCompletoDeJogoComToken() {
        UsuarioCadastroRequest cadastroUsuario = novoUsuario("jogo-crud");
        Response cadastroResponse = usuarioClient.cadastrar(cadastroUsuario);
        EvidenceUtils.save("fluxo-cadastrar-usuario", "POST", "/usuarios/cadastrar", cadastroUsuario, cadastroResponse);

        cadastroResponse.then()
                .statusCode(200)
                .body("id", notNullValue())
                .body("email", equalTo(cadastroUsuario.email()));

        UsuarioLoginRequest loginRequest = new UsuarioLoginRequest(cadastroUsuario.email(), cadastroUsuario.senha());
        Response loginResponse = usuarioClient.logar(loginRequest);
        EvidenceUtils.save("fluxo-logar-usuario", "POST", "/usuarios/logar", loginRequest, loginResponse);

        loginResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("email", equalTo(cadastroUsuario.email()))
                .body("token", startsWith("Bearer "));

        String token = loginResponse.jsonPath().getString("token");
        assertNotNull(token);

        JogoRequest criarJogoRequest = new JogoRequest(
                "Counter-Strike 2 API " + UUID.randomUUID(),
                "https://example.com/cs2.png",
                "Jogo criado por teste automatizado de API"
        );
        Response criarJogoResponse = jogoClient.cadastrar(criarJogoRequest, token);
        EvidenceUtils.save("fluxo-criar-jogo", "POST", "/jogos/cadastrar", criarJogoRequest, criarJogoResponse);

        criarJogoResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("id", notNullValue())
                .body("nome", equalTo(criarJogoRequest.nome()))
                .body("imagemUrl", equalTo(criarJogoRequest.imagemUrl()))
                .body("descricao", equalTo(criarJogoRequest.descricao()));

        Long jogoId = criarJogoResponse.jsonPath().getLong("id");
        assertNotNull(jogoId);

        Response buscarJogoResponse = jogoClient.buscarPorId(jogoId, token);
        EvidenceUtils.save("fluxo-buscar-jogo", "GET", "/jogos/" + jogoId, null, buscarJogoResponse);

        buscarJogoResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("nome", equalTo(criarJogoRequest.nome()));
        assertEquals(jogoId, buscarJogoResponse.jsonPath().getLong("id"));

        JogoRequest atualizarJogoRequest = new JogoRequest(
                criarJogoRequest.nome() + " Atualizado",
                criarJogoRequest.imagemUrl(),
                "Descricao atualizada pelo teste automatizado"
        );
        Response atualizarJogoResponse = jogoClient.atualizar(jogoId, atualizarJogoRequest, token);
        EvidenceUtils.save("fluxo-atualizar-jogo", "PUT", "/jogos/atualizar/" + jogoId, atualizarJogoRequest, atualizarJogoResponse);

        atualizarJogoResponse.then()
                .statusCode(200)
                .contentType(ContentType.JSON)
                .body("nome", equalTo(atualizarJogoRequest.nome()))
                .body("descricao", equalTo(atualizarJogoRequest.descricao()));
        assertEquals(jogoId, atualizarJogoResponse.jsonPath().getLong("id"));

        Response deletarJogoResponse = jogoClient.deletar(jogoId, token);
        EvidenceUtils.save("fluxo-deletar-jogo", "DELETE", "/jogos/" + jogoId, null, deletarJogoResponse);

        deletarJogoResponse.then()
                .statusCode(204);

        Response buscarJogoDeletadoResponse = jogoClient.buscarPorId(jogoId, token);
        EvidenceUtils.save("fluxo-buscar-jogo-deletado", "GET", "/jogos/" + jogoId, null, buscarJogoDeletadoResponse);

        buscarJogoDeletadoResponse.then()
                .statusCode(404);
    }

    private UsuarioCadastroRequest novoUsuario(String prefixo) {
        String sufixo = UUID.randomUUID().toString().replace("-", "");
        return new UsuarioCadastroRequest(
                "api_" + prefixo + "_" + sufixo.substring(0, 8),
                "api." + prefixo + "." + sufixo + "@example.com",
                "Senha@123",
                "USER",
                "https://example.com/avatar.png",
                "Usuario criado por teste automatizado de API"
        );
    }
}
