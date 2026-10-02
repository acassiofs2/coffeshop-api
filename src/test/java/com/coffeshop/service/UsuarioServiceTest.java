package com.coffeshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coffeshop.dto.UsuarioRequestDTO;
import com.coffeshop.dto.UsuarioResponseDTO;
import com.coffeshop.entity.Usuario;
import com.coffeshop.repository.UsuarioRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private UsuarioService usuarioService;

    private UUID usuarioId;
    private Usuario usuario;
    private UsuarioRequestDTO request;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        usuario = new Usuario(
                usuarioId,
                "Ana Souza",
                "ana.souza@email.com",
                LocalDateTime.of(2026, 1, 15, 10, 0)
        );
        request = new UsuarioRequestDTO("Ana Souza", "ana.souza@email.com");
    }

    @Test
    @DisplayName("Deve criar um usuário com sucesso")
    void deveCriarUsuario() {
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> {
            Usuario salvo = invocation.getArgument(0);
            salvo.setId(usuarioId);
            salvo.setDataCriacao(LocalDateTime.of(2026, 1, 15, 10, 0));
            return salvo;
        });

        UsuarioResponseDTO response = usuarioService.criar(request);

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());

        assertThat(captor.getValue().getNome()).isEqualTo("Ana Souza");
        assertThat(captor.getValue().getEmail()).isEqualTo("ana.souza@email.com");
        assertThat(response.getId()).isEqualTo(usuarioId);
        assertThat(response.getNome()).isEqualTo("Ana Souza");
        assertThat(response.getEmail()).isEqualTo("ana.souza@email.com");
    }

    @Test
    @DisplayName("Deve listar todos os usuários")
    void deveListarUsuarios() {
        when(usuarioRepository.findAll()).thenReturn(List.of(usuario));

        List<UsuarioResponseDTO> response = usuarioService.listar();

        assertThat(response).hasSize(1);
        assertThat(response.getFirst().getId()).isEqualTo(usuarioId);
        assertThat(response.getFirst().getNome()).isEqualTo("Ana Souza");
        verify(usuarioRepository).findAll();
    }

    @Test
    @DisplayName("Deve buscar usuário por ID com sucesso")
    void deveBuscarPorId() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));

        UsuarioResponseDTO response = usuarioService.buscarPorId(usuarioId);

        assertThat(response.getId()).isEqualTo(usuarioId);
        assertThat(response.getEmail()).isEqualTo("ana.souza@email.com");
        verify(usuarioRepository).findById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar ID inexistente")
    void deveLancarNotFoundAoBuscarIdInexistente() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.buscarPorId(usuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    @DisplayName("Deve atualizar um usuário com sucesso")
    void deveAtualizarUsuario() {
        UsuarioRequestDTO updateRequest = new UsuarioRequestDTO("Ana Silva", "ana.silva@email.com");
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(usuarioRepository.save(any(Usuario.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UsuarioResponseDTO response = usuarioService.atualizar(usuarioId, updateRequest);

        assertThat(response.getNome()).isEqualTo("Ana Silva");
        assertThat(response.getEmail()).isEqualTo("ana.silva@email.com");
        verify(usuarioRepository).save(usuario);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atualizar ID inexistente")
    void deveLancarNotFoundAoAtualizarIdInexistente() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.atualizar(usuarioId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });

        verify(usuarioRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar um usuário com sucesso")
    void deveDeletarUsuario() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(true);

        usuarioService.deletar(usuarioId);

        verify(usuarioRepository).deleteById(usuarioId);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar ID inexistente")
    void deveLancarNotFoundAoDeletarIdInexistente() {
        when(usuarioRepository.existsById(usuarioId)).thenReturn(false);

        assertThatThrownBy(() -> usuarioService.deletar(usuarioId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });

        verify(usuarioRepository, never()).deleteById(any());
    }
}
