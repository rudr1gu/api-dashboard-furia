# Análise técnica do repositório `api-dashboard-furia`

## 1) Objetivo do projeto

Este projeto implementa uma API REST para uma comunidade gamer (Dashboard FURIA), cobrindo:

- cadastro/autenticação de usuários;
- catálogo de jogos e níveis;
- conteúdo social (postagens, respostas e interações);
- eventos e redes sociais associadas aos usuários.

A arquitetura segue o padrão clássico de camadas: **Controller → Service → Repository → Model**.

---

## 2) Stack e dependências

### Backend

- Java 17 (`pom.xml`)
- Spring Boot 3.4.0
- Spring Web
- Spring Data JPA
- Spring Validation
- Spring Security
- JWT com `io.jsonwebtoken`
- Springdoc OpenAPI (Swagger)

### Persistência

- MySQL (`application-dev.properties`)
- PostgreSQL (`application-prod.properties`)

### Build e execução

- Maven Wrapper (`mvnw`) + Maven tradicional
- Dockerfile multi-stage para build da aplicação e execução do jar explodido

---

## 3) Estrutura de código

```text
src/main/java/com/conectados/conectados
├── configuration  # Config Swagger/OpenAPI
├── controller     # Endpoints REST
├── domain
│   ├── model      # Entidades JPA
│   └── repository # Repositórios Spring Data
├── security       # JWT + filtro + UserDetails
└── services       # Regras de negócio
```

Pontos positivos da organização:

- separação clara por responsabilidade;
- nomenclatura consistente entre recursos (controllers/services/repositories);
- modelos com anotações JPA e validação em campos críticos.

---

## 4) Modelo de domínio

As principais entidades são:

- `Usuario`
- `Nivel`
- `Jogo`
- `Postagem`
- `Resposta`
- `Interacao`
- `Evento`
- `RedeSocial`

### Relacionamentos relevantes

- `Usuario` → `Nivel` (**ManyToOne**)
- `Usuario` ↔ `Jogo` (**ManyToMany** via tabela `usuario_jogo`)
- `Usuario` → `Postagem`, `Resposta`, `RedeSocial` (**OneToMany**)
- `Postagem` → `Resposta` (**OneToMany**)
- `Interacao` referencia `Usuario` e `Postagem` (**ManyToOne**)

### Observações de modelagem

- Há uso de `@JsonIgnoreProperties` para evitar recursão infinita na serialização.
- Em `Evento`, campos de data/hora estão como `String`; para consistência, poderia evoluir para `LocalDate`/`LocalDateTime`.
- Em geral, IDs são `Long` com `GenerationType.IDENTITY`.

---

## 5) Segurança e autenticação

### Estratégia atual

- aplicação stateless (`SessionCreationPolicy.STATELESS`);
- JWT validado por `JwtAuthFilter` (header `Authorization: Bearer ...`);
- autenticação de credenciais via `AuthenticationManager` em `/usuarios/logar`.

### Endpoints públicos (sem token)

- `POST /usuarios/logar`
- `POST /usuarios/cadastrar`
- `GET /jogos/all`
- `OPTIONS` e `/error/**`

Todos os demais endpoints são autenticados por padrão.

### Pontos de atenção

- A chave secreta JWT está hardcoded em código-fonte (`JwtService.SECRET`): ideal mover para variável de ambiente.
- O token expira em 1 hora, sem mecanismo de refresh token.

---

## 6) API e padronização dos endpoints

A API é coerente no padrão por recurso:

- `GET /<recurso>/all`
- `GET /<recurso>/{id}`
- `POST /<recurso>/cadastrar`
- `PUT /<recurso>/atualizar/{id}` (em `Usuario`, `PUT /usuarios/atualizar`)
- `DELETE /<recurso>/{id}`

Recursos implementados:

- `usuarios`
- `jogos`
- `niveis`
- `postagens`
- `respostas`
- `interacoes`
- `eventos`
- `redes-sociais`

Swagger/OpenAPI está configurado para varrer os controllers e disponibilizar docs em `/swagger-ui.html`.

---

## 7) Regras de negócio atuais

### Usuários

- cadastro impede e-mail duplicado;
- senha é criptografada com BCrypt no cadastro e atualização;
- login retorna payload com token JWT e dados resumidos do usuário.

### Demais recursos

- serviços implementam CRUD básico;
- em deleção, há verificação de existência e exceção em recurso não encontrado.

### Ponto de consistência

Nos endpoints `GET /all`, quando não existem dados, os services retornam `Optional.empty()` e o controller responde `200` com corpo `null`. Uma alternativa mais REST seria:

- `200` com lista vazia `[]` (recomendado para coleções), ou
- `204 No Content`.

---

## 8) Qualidade, testes e observabilidade

### Testes

Há testes iniciais em `src/test`, mas sem cobertura ampla de regras e cenários de API.

### Oportunidades de melhoria

1. Expandir testes:
   - unitários para serviços;
   - integração para controllers/security (MockMvc);
   - cenários de autenticação JWT.
2. Tratar exceções globalmente (`@ControllerAdvice`) para respostas de erro padronizadas.
3. Adicionar migrations (Flyway/Liquibase) para versionamento de schema.
4. Adotar DTOs para entrada/saída, reduzindo acoplamento entre entidade e contrato da API.
5. Externalizar segredos (JWT SECRET e credenciais) e reforçar hardening de produção.

---

## 9) Executando em container

O `Dockerfile` é multi-stage:

1. estágio de build com Maven Wrapper (`./mvnw install -DskipTests`);
2. estágio final com classes e libs copiadas para `/app`;
3. entrada via `java -cp app:app/lib/* com.conectados.conectados.ConectadosApplication`.

É uma base funcional; para produção, vale considerar:

- imagem base mais enxuta (ex.: Eclipse Temurin);
- usuário não-root;
- healthcheck e parâmetros de JVM.

---

## 10) Conclusão

O repositório já apresenta uma base sólida para API social/gamer com:

- boa separação em camadas;
- autenticação JWT funcional;
- modelagem de domínio coerente para o caso de uso;
- documentação automática via Swagger.

As próximas evoluções prioritárias para maturidade seriam:

1. reforço de segurança (segredos/refresh token);
2. melhoria de consistência de respostas e tratamento de erros;
3. ampliação da suíte de testes e governança de schema.
