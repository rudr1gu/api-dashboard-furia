package com.conectados.conectados.api.config;

import com.conectados.conectados.api.client.JogoClient;
import com.conectados.conectados.api.client.UsuarioClient;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public abstract class ApiTestConfig {

    @LocalServerPort
    private int port;

    protected UsuarioClient usuarioClient;
    protected JogoClient jogoClient;

    @BeforeEach
    void configureApiTest() {
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = port;
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        RequestSpecification requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .setAccept(ContentType.JSON)
                .build();

        usuarioClient = new UsuarioClient(requestSpecification);
        jogoClient = new JogoClient(requestSpecification);
    }
}
