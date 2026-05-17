package com.conectados.conectados.api.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.restassured.http.Header;
import io.restassured.response.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public final class EvidenceUtils {

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final Path EVIDENCES_DIR = Path.of("target", "evidences");
    private static final DateTimeFormatter FILE_TIMESTAMP = DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    private EvidenceUtils() {
    }

    public static void save(String stepName, String method, String endpoint, Object requestBody, Response response) {
        try {
            Files.createDirectories(EVIDENCES_DIR);

            Map<String, Object> evidence = new LinkedHashMap<>();
            evidence.put("step", stepName);
            evidence.put("method", method);
            evidence.put("endpoint", endpoint);
            evidence.put("request", requestBody);
            evidence.put("statusCode", response.statusCode());
            evidence.put("headers", responseHeaders(response));
            evidence.put("response", parseResponseBody(response.asString()));

            String fileName = FILE_TIMESTAMP.format(LocalDateTime.now()) + "-" + sanitize(stepName) + ".json";
            OBJECT_MAPPER.writerWithDefaultPrettyPrinter()
                    .writeValue(EVIDENCES_DIR.resolve(fileName).toFile(), evidence);
        } catch (IOException exception) {
            throw new IllegalStateException("Nao foi possivel salvar evidencia da API", exception);
        }
    }

    private static Map<String, String> responseHeaders(Response response) {
        Map<String, String> headers = new LinkedHashMap<>();
        for (Header header : response.getHeaders()) {
            headers.put(header.getName(), header.getValue());
        }
        return headers;
    }

    private static Object parseResponseBody(String body) {
        if (body == null || body.isBlank()) {
            return null;
        }

        try {
            JsonNode jsonNode = OBJECT_MAPPER.readTree(body);
            return jsonNode;
        } catch (JsonProcessingException exception) {
            return body;
        }
    }

    private static String sanitize(String value) {
        return value.replaceAll("[^a-zA-Z0-9-_]", "-");
    }
}
