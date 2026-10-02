package com.coffeshop.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.coffeshop.dto.LojaRequestDTO;
import com.coffeshop.dto.LojaResponseDTO;
import com.coffeshop.entity.Loja;
import com.coffeshop.entity.Usuario;
import com.coffeshop.repository.LojaRepository;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class LojaServiceTest {

    @Mock
    private LojaRepository lojaRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private LojaService lojaService;

    private UUID lojaId;
    private UUID usuarioId;
    private Usuario usuario;
    private Loja loja;
    private LojaRequestDTO request;

    @BeforeEach
    void setUp() {
        lojaId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        usuario = new Usuario(
                usuarioId,
                "Ana Souza",
                "ana.souza@email.com",
                LocalDateTime.of(2026, 1, 10, 9, 0)
        );
        loja = new Loja(
                lojaId,
                "Coffee Shop Centro",
                "Rua das Flores, 100",
                "(11) 98765-4321",
                usuario,
                LocalDateTime.of(2026, 1, 15, 10, 0)
        );
        request = new LojaRequestDTO(
                "Coffee Shop Centro",
                "Rua das Flores, 100",
                "(11) 98765-4321",
                usuarioId
        );
    }

    @Test
    @DisplayName("Deve criar uma loja com sucesso")
    void deveCriarLoja() {
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocation -> {
            Loja salva = invocation.getArgument(0);
            salva.setId(lojaId);
            salva.setDataCriacao(LocalDateTime.of(2026, 1, 15, 10, 0));
            return salva;
        });

        LojaResponseDTO response = lojaService.criar(request);

        ArgumentCaptor<Loja> captor = ArgumentCaptor.forClass(Loja.class);
        verify(lojaRepository).save(captor.capture());

        assertThat(captor.getValue().getNome()).isEqualTo("Coffee Shop Centro");
        assertThat(captor.getValue().getEndereco()).isEqualTo("Rua das Flores, 100");
        assertThat(captor.getValue().getTelefone()).isEqualTo("(11) 98765-4321");
        assertThat(captor.getValue().getUsuario().getId()).isEqualTo(usuarioId);
        assertThat(response.getId()).isEqualTo(lojaId);
        assertThat(response.getNome()).isEqualTo("Coffee Shop Centro");
        assertThat(response.getEndereco()).isEqualTo("Rua das Flores, 100");
        assertThat(response.getTelefone()).isEqualTo("(11) 98765-4321");
        assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
    }

    @Test
    @DisplayName("Deve listar lojas de forma paginada")
    void deveListarLojas() {
        Pageable pageable = PageRequest.of(0, 20);
        when(lojaRepository.findAllByOrderByNomeAsc(pageable))
                .thenReturn(new PageImpl<>(List.of(loja), pageable, 1));

        Page<LojaResponseDTO> response = lojaService.listar(pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.getContent().getFirst().getId()).isEqualTo(lojaId);
        assertThat(response.getContent().getFirst().getNome()).isEqualTo("Coffee Shop Centro");
        assertThat(response.getContent().getFirst().getUsuarioId()).isEqualTo(usuarioId);
        verify(lojaRepository).findAllByOrderByNomeAsc(pageable);
    }

    @Test
    @DisplayName("Deve buscar loja por ID com sucesso")
    void deveBuscarPorId() {
        when(lojaRepository.findById(lojaId)).thenReturn(Optional.of(loja));

        LojaResponseDTO response = lojaService.buscarPorId(lojaId);

        assertThat(response.getId()).isEqualTo(lojaId);
        assertThat(response.getEndereco()).isEqualTo("Rua das Flores, 100");
        assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
        verify(lojaRepository).findById(lojaId);
    }

    @Test
    @DisplayName("Deve lançar 404 ao buscar ID inexistente")
    void deveLancarNotFoundAoBuscarIdInexistente() {
        when(lojaRepository.findById(lojaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lojaService.buscarPorId(lojaId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });
    }

    @Test
    @DisplayName("Deve atualizar uma loja com sucesso")
    void deveAtualizarLoja() {
        LojaRequestDTO updateRequest = new LojaRequestDTO(
                "Coffee Shop Jardins",
                "Av. Paulista, 500",
                "(11) 91234-5678",
                usuarioId
        );
        when(lojaRepository.findById(lojaId)).thenReturn(Optional.of(loja));
        when(usuarioRepository.findById(usuarioId)).thenReturn(Optional.of(usuario));
        when(lojaRepository.save(any(Loja.class))).thenAnswer(invocation -> invocation.getArgument(0));

        LojaResponseDTO response = lojaService.atualizar(lojaId, updateRequest);

        assertThat(response.getNome()).isEqualTo("Coffee Shop Jardins");
        assertThat(response.getEndereco()).isEqualTo("Av. Paulista, 500");
        assertThat(response.getTelefone()).isEqualTo("(11) 91234-5678");
        assertThat(response.getUsuarioId()).isEqualTo(usuarioId);
        verify(lojaRepository).save(loja);
    }

    @Test
    @DisplayName("Deve lançar 404 ao atualizar ID inexistente")
    void deveLancarNotFoundAoAtualizarIdInexistente() {
        when(lojaRepository.findById(lojaId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> lojaService.atualizar(lojaId, request))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });

        verify(lojaRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve deletar uma loja com sucesso")
    void deveDeletarLoja() {
        when(lojaRepository.existsById(lojaId)).thenReturn(true);

        lojaService.deletar(lojaId);

        verify(lojaRepository).deleteById(lojaId);
    }

    @Test
    @DisplayName("Deve lançar 404 ao deletar ID inexistente")
    void deveLancarNotFoundAoDeletarIdInexistente() {
        when(lojaRepository.existsById(lojaId)).thenReturn(false);

        assertThatThrownBy(() -> lojaService.deletar(lojaId))
                .isInstanceOf(ResponseStatusException.class)
                .satisfies(ex -> {
                    ResponseStatusException exception = (ResponseStatusException) ex;
                    assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                });

        verify(lojaRepository, never()).deleteById(any());
    }
}
