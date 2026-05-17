package com.conectados.conectados.api.client;

import com.conectados.conectados.api.payload.requests.JogoRequest;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

public class JogoClient {

    private final RequestSpecification requestSpecification;

    public JogoClient(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification;
    }

    public Response listar() {
        return given()
                .spec(requestSpecification)
                .when()
                .get("/jogos/all");
    }

    public Response cadastrar(JogoRequest request, String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", token)
                .body(request)
                .when()
                .post("/jogos/cadastrar");
    }

    public Response buscarPorId(Long id, String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", token)
                .when()
                .get("/jogos/{id}", id);
    }

    public Response atualizar(Long id, JogoRequest request, String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", token)
                .body(request)
                .when()
                .put("/jogos/atualizar/{id}", id);
    }

    public Response deletar(Long id, String token) {
        return given()
                .spec(requestSpecification)
                .header("Authorization", token)
                .when()
                .delete("/jogos/{id}", id);
    }
}
