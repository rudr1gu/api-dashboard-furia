# Proposta de estrutura com Arquitetura Hexagonal + DTOs

Este documento mostra como você pode reorganizar este projeto para **arquitetura hexagonal (ports & adapters)**, separando regra de negócio do framework e da infraestrutura.

## 1) Visão rápida

Na arquitetura hexagonal, o núcleo da aplicação não conhece Spring, banco, HTTP ou JWT diretamente.

- **Domínio**: entidades e regras de negócio puras.
- **Aplicação**: casos de uso (use cases), portas de entrada/saída e DTOs.
- **Adapters**: implementação das portas para web, banco, segurança, mensageria etc.
- **Bootstrap/Config**: inicialização do Spring.

---

## 2) Estrutura de pastas sugerida

```text
src/main/java/com/conectados/conectados
├── bootstrap
│   ├── ConectadosApplication.java
│   └── config
│       ├── OpenApiConfig.java
│       ├── SecurityConfig.java
│       └── BeanConfig.java
│
├── domain
│   ├── model
│   │   ├── Usuario.java
│   │   ├── Jogo.java
│   │   ├── Postagem.java
│   │   ├── Resposta.java
│   │   ├── Interacao.java
│   │   ├── Evento.java
│   │   ├── RedeSocial.java
│   │   └── Nivel.java
│   ├── service
│   │   └── UsuarioDomainService.java
│   └── exception
│       ├── DomainException.java
│       └── UsuarioJaExisteException.java
│
├── application
│   ├── port
│   │   ├── in
│   │   │   ├── usuario
│   │   │   │   ├── CadastrarUsuarioUseCase.java
│   │   │   │   ├── AtualizarUsuarioUseCase.java
│   │   │   │   ├── BuscarUsuarioUseCase.java
│   │   │   │   └── AutenticarUsuarioUseCase.java
│   │   │   └── postagem
│   │   │       ├── CriarPostagemUseCase.java
│   │   │       └── ListarPostagensUseCase.java
│   │   └── out
│   │       ├── usuario
│   │       │   ├── UsuarioRepositoryPort.java
│   │       │   ├── PasswordEncoderPort.java
│   │       │   └── TokenServicePort.java
│   │       └── postagem
│   │           └── PostagemRepositoryPort.java
│   │
│   ├── usecase
│   │   ├── usuario
│   │   │   ├── CadastrarUsuarioService.java
│   │   │   ├── AtualizarUsuarioService.java
│   │   │   ├── BuscarUsuarioService.java
│   │   │   └── AutenticarUsuarioService.java
│   │   └── postagem
│   │       ├── CriarPostagemService.java
│   │       └── ListarPostagensService.java
│   │
│   ├── dto
│   │   ├── in
│   │   │   ├── usuario
│   │   │   │   ├── CadastrarUsuarioRequest.java
│   │   │   │   ├── AtualizarUsuarioRequest.java
│   │   │   │   └── LoginRequest.java
│   │   │   └── postagem
│   │   │       └── CriarPostagemRequest.java
│   │   └── out
│   │       ├── usuario
│   │       │   ├── UsuarioResponse.java
│   │       │   └── LoginResponse.java
│   │       └── postagem
│   │           └── PostagemResponse.java
│   │
│   └── mapper
│       ├── UsuarioMapper.java
│       └── PostagemMapper.java
│
└── adapter
    ├── in
    │   └── web
    │       ├── UsuarioController.java
    │       ├── PostagemController.java
    │       └── advice
    │           └── GlobalExceptionHandler.java
    │
    └── out
        ├── persistence
        │   ├── jpa
        │   │   ├── entity
        │   │   │   ├── UsuarioJpaEntity.java
        │   │   │   └── PostagemJpaEntity.java
        │   │   ├── repository
        │   │   │   ├── SpringUsuarioRepository.java
        │   │   │   └── SpringPostagemRepository.java
        │   │   └── adapter
        │   │       ├── UsuarioPersistenceAdapter.java
        │   │       └── PostagemPersistenceAdapter.java
        │   └── mapper
        │       ├── UsuarioPersistenceMapper.java
        │       └── PostagemPersistenceMapper.java
        │
        ├── security
        │   ├── JwtTokenAdapter.java
        │   ├── BCryptPasswordEncoderAdapter.java
        │   └── JwtAuthFilter.java
        │
        └── docs
            └── SwaggerAdapterConfig.java
```

---

## 3) Onde entram os DTOs?

Uma forma didática e escalável é:

- `application/dto/in`: payloads de entrada dos casos de uso (requests).
- `application/dto/out`: payloads de saída dos casos de uso (responses).

Assim você evita expor entidade de domínio diretamente na API.

### Exemplo

- `LoginRequest` (entrada do endpoint) → caso de uso `AutenticarUsuarioUseCase`
- caso de uso retorna `LoginResponse`
- controller só orquestra HTTP ↔ use case, sem regra de negócio.

---

## 4) Fluxo de chamada (hexagonal)

```text
Controller (adapter in/web)
  -> UseCase (application/port/in)
  -> Service (application/usecase)
  -> RepositoryPort / TokenPort / PasswordPort (application/port/out)
  -> Adapter de persistência/segurança (adapter/out)
  -> Infra (JPA, PostgreSQL, JWT, BCrypt)
```

Regra importante: **domínio e aplicação não dependem de adapters**.

---

## 5) Mapeamento do projeto atual para o novo desenho

- `controller/*` atual → `adapter/in/web/*`
- `services/*` atual → dividir em `application/usecase/*` + `domain/service/*`
- `domain/model/*` atual → permanece em `domain/model/*`
- `domain/repository/*` atual → vira `application/port/out/*` (interfaces) + implementações em `adapter/out/persistence/*`
- `security/*` atual → parte em `bootstrap/config` e parte em `adapter/out/security`

---

## 6) Organização por feature (opção alternativa)

Se preferir maior coesão por contexto funcional, pode organizar por feature dentro de `application` e `adapter`:

```text
application
└── usuario
    ├── dto
    ├── port
    └── usecase

adapter
├── in/web/usuario
└── out/persistence/usuario
```

Essa abordagem facilita evolução por domínio (ex.: equipe focada em `usuario` ou `postagem`).

---

## 7) Plano de migração incremental (sem big bang)

1. Criar `application/port/in` para 1 fluxo (ex.: login).
2. Extrair interface de saída (`TokenServicePort`, `UsuarioRepositoryPort`).
3. Implementar adapters out mantendo JPA/Spring atuais.
4. Trocar controller para chamar UseCase em vez de Service legado.
5. Repetir para cada recurso (`jogos`, `postagens`, `eventos`...).

Assim você reduz risco e continua entregando funcionalidade enquanto migra.
