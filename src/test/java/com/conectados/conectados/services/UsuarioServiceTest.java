package com.conectados.conectados.services;

import com.conectados.conectados.domain.model.Usuario;
import com.conectados.conectados.domain.model.UsuarioLogin;
import com.conectados.conectados.domain.repository.UsuarioRepository;
import com.conectados.conectados.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private JwtService jwtService;

    @Mock
    private AuthenticationManager authenticationManager;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveRetornarUsuariosQuandoExistirem() {
        Usuario usuario = usuario(1L, "furia", "furia@example.com", "Senha@123");
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        Optional<List<Usuario>> resultado = usuarioService.buscarTodos();

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().size());
        assertSame(usuario, resultado.get().get(0));
        verify(usuarioRepository).findAll();
    }

    @Test
    void deveRetornarOptionalVazioQuandoNaoExistiremUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of());

        Optional<List<Usuario>> resultado = usuarioService.buscarTodos();

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findAll();
    }

    @Test
    void deveCadastrarUsuarioComSenhaCriptografadaQuandoEmailNaoExiste() {
        Usuario usuario = usuario(null, "novo_usuario", "novo.usuario@example.com", "Senha@123");
        when(usuarioRepository.findByEmail(usuario.getEmail())).thenReturn(Optional.empty());
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario usuarioSalvo = invocation.getArgument(0);
            usuarioSalvo.setId(1L);
            return usuarioSalvo;
        });

        Optional<Usuario> resultado = usuarioService.cadastrarUsuario(usuario);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertNotEquals("Senha@123", resultado.get().getSenha());
        assertTrue(new BCryptPasswordEncoder().matches("Senha@123", resultado.get().getSenha()));
        verify(usuarioRepository).findByEmail("novo.usuario@example.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void naoDeveCadastrarUsuarioQuandoEmailJaExiste() {
        Usuario usuarioExistente = usuario(1L, "existente", "existente@example.com", "senha");
        Usuario novoUsuario = usuario(null, "novo", "existente@example.com", "Senha@123");
        when(usuarioRepository.findByEmail("existente@example.com")).thenReturn(Optional.of(usuarioExistente));

        Optional<Usuario> resultado = usuarioService.cadastrarUsuario(novoUsuario);

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findByEmail("existente@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveAtualizarUsuarioQuandoUsuarioExiste() {
        Usuario usuario = usuario(1L, "atualizado", "atualizado@example.com", "NovaSenha@123");
        Usuario usuarioExistente = usuario(1L, "antigo", "atualizado@example.com", "senha-antiga");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.findByEmail("atualizado@example.com")).thenReturn(Optional.of(usuarioExistente));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Optional<Usuario> resultado = usuarioService.atualizarUsuario(usuario);

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("atualizado", resultado.get().getNickName());
        assertNotEquals("NovaSenha@123", resultado.get().getSenha());
        assertTrue(new BCryptPasswordEncoder().matches("NovaSenha@123", resultado.get().getSenha()));
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).findByEmail("atualizado@example.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    void deveLancarBadRequestQuandoAtualizarComEmailDeOutroUsuario() {
        Usuario usuario = usuario(1L, "usuario", "duplicado@example.com", "Senha@123");
        Usuario usuarioComMesmoEmail = usuario(2L, "outro", "duplicado@example.com", "OutraSenha@123");
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.findByEmail("duplicado@example.com")).thenReturn(Optional.of(usuarioComMesmoEmail));

        ResponseStatusException exception = assertThrows(
                ResponseStatusException.class,
                () -> usuarioService.atualizarUsuario(usuario)
        );

        assertEquals(HttpStatus.BAD_REQUEST, exception.getStatusCode());
        verify(usuarioRepository).findById(1L);
        verify(usuarioRepository).findByEmail("duplicado@example.com");
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveRetornarOptionalVazioQuandoAtualizarUsuarioInexistente() {
        Usuario usuario = usuario(99L, "inexistente", "inexistente@example.com", "Senha@123");
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Usuario> resultado = usuarioService.atualizarUsuario(usuario);

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository).findById(99L);
        verify(usuarioRepository, never()).findByEmail(usuario.getEmail());
        verify(usuarioRepository, never()).save(any(Usuario.class));
    }

    @Test
    void deveAutenticarUsuarioComSucessoEPreencherDadosDoLogin() {
        Usuario usuario = usuario(1L, "furioso", "furioso@example.com", "senha-criptografada");
        UsuarioLogin login = login("furioso@example.com", "Senha@123");
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(usuarioRepository.findByEmail("furioso@example.com")).thenReturn(Optional.of(usuario));
        when(jwtService.generateToken("furioso@example.com")).thenReturn("jwt-token");

        Optional<UsuarioLogin> resultado = usuarioService.autenticarUsuario(Optional.of(login));

        assertTrue(resultado.isPresent());
        assertEquals(1L, resultado.get().getId());
        assertEquals("furioso", resultado.get().getNome());
        assertEquals("https://example.com/avatar.png", resultado.get().getFoto());
        assertEquals("USER", resultado.get().getTipo());
        assertEquals("Bearer jwt-token", resultado.get().getToken());
        assertEquals("", resultado.get().getSenha());

        ArgumentCaptor<UsernamePasswordAuthenticationToken> credenciaisCaptor =
                ArgumentCaptor.forClass(UsernamePasswordAuthenticationToken.class);
        verify(authenticationManager).authenticate(credenciaisCaptor.capture());
        assertEquals("furioso@example.com", credenciaisCaptor.getValue().getPrincipal());
        assertEquals("Senha@123", credenciaisCaptor.getValue().getCredentials());
        verify(jwtService).generateToken("furioso@example.com");
    }

    @Test
    void deveRetornarOptionalVazioQuandoAutenticacaoNaoEstiverAutenticada() {
        UsuarioLogin login = login("nao.autenticado@example.com", "Senha@123");
        Authentication authentication = org.mockito.Mockito.mock(Authentication.class);

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(false);

        Optional<UsuarioLogin> resultado = usuarioService.autenticarUsuario(Optional.of(login));

        assertTrue(resultado.isEmpty());
        verify(usuarioRepository, never()).findByEmail("nao.autenticado@example.com");
        verify(jwtService, never()).generateToken(any(String.class));
    }

    private Usuario usuario(Long id, String nickName, String email, String senha) {
        Usuario usuario = new Usuario();
        usuario.setId(id);
        usuario.setNickName(nickName);
        usuario.setEmail(email);
        usuario.setSenha(senha);
        usuario.setTipo("USER");
        usuario.setAvatar("https://example.com/avatar.png");
        usuario.setBio("Bio de teste");
        return usuario;
    }

    private UsuarioLogin login(String email, String senha) {
        UsuarioLogin login = new UsuarioLogin();
        login.setEmail(email);
        login.setSenha(senha);
        return login;
    }
}
