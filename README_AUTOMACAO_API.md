# Automacao de API com RestAssured

Este README explica como funciona a automacao de testes de endpoints criada neste projeto.

A ideia e testar a API como um consumidor real faria: o teste sobe a aplicacao, faz chamadas HTTP nos endpoints, valida as respostas e salva evidencias simples em JSON.

## Tecnologias usadas

- Java 17
- Spring Boot Test
- JUnit 5
- RestAssured
- H2 em memoria para os testes
- Maven

O `spring-boot-starter-test` ja fornece o JUnit 5. As dependencias novas da automacao sao:

- `io.rest-assured:rest-assured`
- `com.h2database:h2`

Elas ficam no `pom.xml` com `scope test`, ou seja, sao usadas apenas durante os testes.

## Como rodar

Na raiz do projeto, execute:

```bash
mvn test
```

No Windows, tambem pode usar o Maven Wrapper:

```bash
.\mvnw.cmd test
```

Ao rodar esse comando, o Maven:

1. compila o projeto;
2. sobe a aplicacao Spring Boot em uma porta aleatoria;
3. usa o perfil `test`;
4. cria um banco H2 em memoria;
5. executa os testes de API;
6. salva evidencias em `target/evidences/`.

## Perfil de teste

Os testes usam arquivos proprios em `src/test/resources`:

```text
src/test/resources
|-- application.properties
|-- application-test.properties
```

O arquivo `application.properties` ativa o perfil:

```properties
spring.profiles.active=test
```

O arquivo `application-test.properties` configura o H2:

```properties
spring.datasource.url=jdbc:h2:mem:conectados_test;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=false
```

Isso evita depender do MySQL local. Cada execucao cria um banco temporario e descarta tudo no final.

## Estrutura criada

A automacao fica em:

```text
src/test/java/com/conectados/conectados/api
|-- config
|   |-- ApiTestConfig.java
|-- client
|   |-- UsuarioClient.java
|   |-- JogoClient.java
|-- payload
|   |-- requests
|       |-- UsuarioCadastroRequest.java
|       |-- UsuarioLoginRequest.java
|       |-- JogoRequest.java
|-- tests
|   |-- JogoApiTest.java
|-- utils
    |-- EvidenceUtils.java
```

## Papel de cada camada

### `ApiTestConfig`

Classe base dos testes de API.

Ela faz a configuracao comum do RestAssured:

- `baseURI` como `http://localhost`;
- porta aleatoria recebida pelo Spring Boot;
- `Content-Type: application/json`;
- `Accept: application/json`;
- log automatico quando uma validacao falha.

Tambem inicializa os clients:

- `UsuarioClient`
- `JogoClient`

### Clients

Os clients concentram as chamadas HTTP.

Exemplo: o teste nao precisa escrever toda hora `given().body(...).post(...)`. Ele chama um metodo mais legivel:

```java
Response response = usuarioClient.cadastrar(request);
```

No projeto existem dois clients iniciais:

- `UsuarioClient`: chama `/usuarios/cadastrar` e `/usuarios/logar`;
- `JogoClient`: chama `/jogos/all`, `/jogos/cadastrar`, `/jogos/{id}`, `/jogos/atualizar/{id}` e `DELETE /jogos/{id}`.

### Payloads

Os payloads representam o corpo enviado nas requisicoes.

Foram usados `records` do Java para deixar o codigo simples:

```java
public record JogoRequest(
        String nome,
        String imagemUrl,
        String descricao
) {
}
```

Na pratica, esse objeto vira JSON quando o RestAssured envia a requisicao.

### Testes

A classe principal e `JogoApiTest`.

Ela tem dois testes:

- `deveCadastrarUsuarioComSucesso`
- `deveExecutarFluxoCompletoDeJogoComToken`

O primeiro teste e simples: cadastra um usuario e valida a resposta.

O segundo teste executa um fluxo completo:

1. cadastra um usuario;
2. faz login;
3. pega o token da resposta;
4. cria um jogo;
5. busca o jogo pelo ID;
6. atualiza o jogo;
7. deleta o jogo;
8. tenta buscar novamente e espera `404`.

## Fluxo de execucao

O fluxo mental da automacao e este:

```text
teste -> client -> endpoint -> response -> validacao -> evidencia
```

Exemplo pratico:

1. O teste cria um `JogoRequest`;
2. chama `jogoClient.cadastrar(request, token)`;
3. o client faz `POST /jogos/cadastrar`;
4. a API responde com status e body;
5. o teste valida `statusCode`, campos obrigatorios e conteudo;
6. `EvidenceUtils` salva o request e response em JSON.

## Validacoes feitas

Os testes validam pontos importantes da resposta HTTP:

- status code, como `200`, `204` e `404`;
- `Content-Type` quando existe body JSON;
- campos obrigatorios, como `id`, `email`, `token` e `nome`;
- valores retornados no body;
- reaproveitamento de dados entre chamadas, como `token` e `id`.

Exemplo de validacao:

```java
response.then()
        .statusCode(200)
        .contentType(ContentType.JSON)
        .body("id", notNullValue())
        .body("nome", equalTo(request.nome()));
```

## Autenticacao no fluxo

O projeto protege a maioria dos endpoints.

Por isso, o fluxo completo primeiro chama:

```text
POST /usuarios/cadastrar
POST /usuarios/logar
```

Depois o teste extrai o token:

```java
String token = loginResponse.jsonPath().getString("token");
```

Esse token ja vem no formato esperado pela API:

```text
Bearer eyJ...
```

Entao ele e enviado no header:

```java
.header("Authorization", token)
```

## Evidencias

As evidencias sao salvas em:

```text
target/evidences/
```

Cada arquivo JSON contem:

- nome da etapa;
- metodo HTTP;
- endpoint;
- request;
- status code;
- headers;
- response.

Exemplo de arquivo gerado:

```text
20260517-111044-381-fluxo-criar-jogo.json
```

Exemplo de conteudo:

```json
{
  "step": "fluxo-criar-jogo",
  "method": "POST",
  "endpoint": "/jogos/cadastrar",
  "request": {
    "nome": "Counter-Strike 2 API",
    "imagemUrl": "https://example.com/cs2.png",
    "descricao": "Jogo criado por teste automatizado de API"
  },
  "statusCode": 200,
  "headers": {
    "Content-Type": "application/json"
  },
  "response": {
    "id": 1,
    "nome": "Counter-Strike 2 API",
    "imagemUrl": "https://example.com/cs2.png",
    "descricao": "Jogo criado por teste automatizado de API"
  }
}
```

## Como ler o teste principal

Abra:

```text
src/test/java/com/conectados/conectados/api/tests/JogoApiTest.java
```

Leia o teste `deveExecutarFluxoCompletoDeJogoComToken` de cima para baixo.

Ele foi escrito para parecer um roteiro:

```text
cadastrar usuario
logar usuario
criar jogo
buscar jogo
atualizar jogo
deletar jogo
confirmar que foi deletado
```

Essa e uma boa forma de aprender automacao de API: cada etapa chama um endpoint real e usa dados da etapa anterior.

## Como evoluir

Proximos passos naturais:

- adicionar testes negativos, como criar jogo sem token e esperar `401` ou `403`;
- validar campos obrigatorios com payload invalido;
- criar clients para `Nivel`, `Evento`, `Postagem` e outros recursos;
- separar massa de dados em factories;
- rodar `mvn test` em uma pipeline de CI.

Para pipeline, a ideia basica e simples:

```text
checkout do codigo -> instalar Java 17 -> rodar mvn test -> publicar target/surefire-reports e target/evidences
```
