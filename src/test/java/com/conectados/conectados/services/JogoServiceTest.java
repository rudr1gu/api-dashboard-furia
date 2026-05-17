package com.conectados.conectados.services;

import com.conectados.conectados.domain.model.Jogo;
import com.conectados.conectados.domain.repository.JogoRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JogoServiceTest {

    @Mock
    private JogoRepository jogoRepository;

    @InjectMocks
    private JogoService jogoService;

    @Test
    void deveRetornarJogosQuandoExistirem() {
        Jogo jogo = jogo(1L, "Counter-Strike 2");
        when(jogoRepository.findAll()).thenReturn(List.of(jogo));

        Optional<List<Jogo>> resultado = jogoService.buscarTodosJogos();

        assertTrue(resultado.isPresent());
        assertEquals(1, resultado.get().size());
        assertSame(jogo, resultado.get().get(0));
        verify(jogoRepository).findAll();
    }

    @Test
    void deveRetornarOptionalVazioQuandoNaoExistiremJogos() {
        when(jogoRepository.findAll()).thenReturn(List.of());

        Optional<List<Jogo>> resultado = jogoService.buscarTodosJogos();

        assertTrue(resultado.isEmpty());
        verify(jogoRepository).findAll();
    }

    @Test
    void deveBuscarJogoPorId() {
        Jogo jogo = jogo(1L, "Valorant");
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        Optional<Jogo> resultado = jogoService.buscarJogoPorId(1L);

        assertTrue(resultado.isPresent());
        assertSame(jogo, resultado.get());
        verify(jogoRepository).findById(1L);
    }

    @Test
    void deveCadastrarJogo() {
        Jogo jogo = jogo(null, "League of Legends");
        Jogo jogoSalvo = jogo(10L, "League of Legends");
        when(jogoRepository.save(jogo)).thenReturn(jogoSalvo);

        Jogo resultado = jogoService.cadastrarJogo(jogo);

        assertSame(jogoSalvo, resultado);
        verify(jogoRepository).save(jogo);
    }

    @Test
    void deveAtualizarJogo() {
        Jogo jogo = jogo(5L, "Rainbow Six Siege");
        when(jogoRepository.save(jogo)).thenReturn(jogo);

        Jogo resultado = jogoService.atualizarJogo(jogo);

        assertSame(jogo, resultado);
        verify(jogoRepository).save(jogo);
    }

    @Test
    void deveDeletarJogoExistente() {
        Jogo jogo = jogo(1L, "Dota 2");
        when(jogoRepository.findById(1L)).thenReturn(Optional.of(jogo));

        jogoService.deletarJogo(1L);

        verify(jogoRepository).findById(1L);
        verify(jogoRepository).deleteById(1L);
    }

    @Test
    void deveLancarErroAoDeletarJogoInexistente() {
        when(jogoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> jogoService.deletarJogo(99L));

        assertFalse(exception.getMessage().isBlank());
        verify(jogoRepository).findById(99L);
        verify(jogoRepository, never()).deleteById(99L);
    }

    private Jogo jogo(Long id, String nome) {
        Jogo jogo = new Jogo();
        jogo.setId(id);
        jogo.setNome(nome);
        jogo.setImagemUrl("https://example.com/" + nome.toLowerCase().replace(" ", "-") + ".png");
        jogo.setDescricao("Descricao de " + nome);
        return jogo;
    }
}
