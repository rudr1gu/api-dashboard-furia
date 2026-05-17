package com.conectados.conectados.api.client;

import com.conectados.conectados.api.payload.requests.UsuarioCadastroRequest;
import com.conectados.conectados.api.payload.requests.UsuarioLoginRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class UsuarioClient {

    private final RequestSpecification requestSpecification;

    public UsuarioClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response cadastrar(UsuarioCadastroRequest request) {
        return given()
                .spec(requestSpecification)
                .body(request)
                .when()
                .post("/usuarios/cadastrar");
    }

    public Response logar(UsuarioLoginRequest request) {
        return given()
                .spec(requestSpecification)
                .body(request)
                .when()
                .post("/usuarios/logar");
    }
}
