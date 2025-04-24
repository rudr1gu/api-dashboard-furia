## Diagrama Entidade Relacionamento 

```mermaid
erDiagram
  USUARIO ||--o{ REDE_SOCIAL : possui
  USUARIO ||--o{ POSTAGEM : cria
  USUARIO ||--o{ RESPOSTA : escreve
  USUARIO ||--o{ INTERACAO : reage
  USUARIO }o--o{ JOGO : joga
  USUARIO }o--o{ EVENTO : participa
  USUARIO }o--o{ CONQUISTA : ganha
  USUARIO }|--|| NIVEL : tem

  POSTAGEM ||--o{ RESPOSTA : recebe
  POSTAGEM ||--o{ INTERACAO : tem
  POSTAGEM }o--|| JOGO : relacionado_a

  REDE_SOCIAL {
    int id
    string plataforma
    string link
  }

  USUARIO {
    int id
    string nome
    string nickname
    string email
    string senha
    string avatar
    datetime data_cadastro
    int nivel_id
  }

  POSTAGEM {
    int id
    text conteudo
    datetime data
  }

  RESPOSTA {
    int id
    text conteudo
    datetime data
  }

  INTERACAO {
    int id
    string tipo
  }

  JOGO {
    int id
    string nome
    string imagem
  }

  EVENTO {
    int id
    string titulo
    text descricao
    datetime data_evento
    string link
    string local
    string imagem
  }

  NIVEL {
    int id
    string nome
  }

  CONQUISTA {
    int id
    string titulo
    text descricao
    string icone
  }

```

## Estruturas das Pastas

```
api-dashboard-furia/
├─ .mvn/
│  └─ wrapper/
│     └─ maven-wrapper.properties
├─ src/
│  ├─ main/
│  │  ├─ java/
│  │  │  └─ com/
│  │  │     └─ conectados/
│  │  │        └─ conectados/
│  │  │           ├─ configuration/
│  │  │           │  └─ SwaggerConfig.java
│  │  │           ├─ controller/
│  │  │           │  ├─ EventoController.java
│  │  │           │  ├─ InteracaoController.java
│  │  │           │  ├─ JogoController.java
│  │  │           │  ├─ NivelController.java
│  │  │           │  ├─ PostagemController.java
│  │  │           │  ├─ RedeSocialController.java
│  │  │           │  ├─ RespostaController.java
│  │  │           │  └─ UsuarioController.java
│  │  │           ├─ domain/
│  │  │           │  ├─ model/
│  │  │           │  │  ├─ Evento.java
│  │  │           │  │  ├─ Interacao.java
│  │  │           │  │  ├─ Jogo.java
│  │  │           │  │  ├─ Nivel.java
│  │  │           │  │  ├─ Postagem.java
│  │  │           │  │  ├─ RedeSocial.java
│  │  │           │  │  ├─ Resposta.java
│  │  │           │  │  ├─ Usuario.java
│  │  │           │  │  └─ UsuarioLogin.java
│  │  │           │  └─ repository/
│  │  │           │     ├─ EventoRepository.java
│  │  │           │     ├─ InteracaoRepository.java
│  │  │           │     ├─ JogoRepository.java
│  │  │           │     ├─ NivelRepository.java
│  │  │           │     ├─ PostagemRepository.java
│  │  │           │     ├─ RedeSocialRepository.java
│  │  │           │     ├─ RespostaRepository.java
│  │  │           │     └─ UsuarioRepository.java
│  │  │           ├─ security/
│  │  │           │  ├─ BasicSecurityConfig.java
│  │  │           │  ├─ JwtAuthFilter.java
│  │  │           │  ├─ JwtService.java
│  │  │           │  ├─ UserDetailsImpl.java
│  │  │           │  └─ UserDetailsServiceImpl.java
│  │  │           ├─ services/
│  │  │           │  ├─ EventoService.java
│  │  │           │  ├─ InteracaoService.java
│  │  │           │  ├─ JogoService.java
│  │  │           │  ├─ NivelService.java
│  │  │           │  ├─ PostagemService.java
│  │  │           │  ├─ RedeSocialService.java
│  │  │           │  ├─ RespostaService.java
│  │  │           │  └─ UsuarioService.java
│  │  │           └─ ConectadosApplication.java
│  │  └─ resources/
│  │     └─ application.properties
│  └─ test/
│     └─ java/
│        └─ com/
│           └─ conectados/
│              └─ conectados/
│                 ├─ usuariotest/
│                 │  └─ UsuarioTest.java
│                 └─ ConectadosApplicationTests.java
├─ .gitattributes
├─ .gitignore
├─ mvnw
├─ mvnw.cmd
├─ pom.xml
└─ README.md

```