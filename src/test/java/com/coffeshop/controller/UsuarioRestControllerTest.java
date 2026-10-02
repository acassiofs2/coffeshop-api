package com.coffeshop.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.coffeshop.dto.UsuarioRequestDTO;
import com.coffeshop.dto.UsuarioResponseDTO;
import com.coffeshop.service.UsuarioService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(UsuarioRestController.class)
class UsuarioRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UsuarioService usuarioService;

    private UUID usuarioId;
    private UsuarioResponseDTO response;
    private UsuarioRequestDTO request;

    @BeforeEach
    void setUp() {
        usuarioId = UUID.randomUUID();
        request = new UsuarioRequestDTO("Ana Souza", "ana.souza@email.com");
        response = new UsuarioResponseDTO(
                usuarioId,
                "Ana Souza",
                "ana.souza@email.com",
                LocalDateTime.of(2026, 1, 15, 10, 0)
        );
    }

    @Test
    @DisplayName("POST /api/usuarios deve retornar 201 Created")
    void deveCriarUsuario() throws Exception {
        when(usuarioService.criar(any(UsuarioRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/usuarios/" + usuarioId))
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.nome").value("Ana Souza"))
                .andExpect(jsonPath("$.email").value("ana.souza@email.com"));
    }

    @Test
    @DisplayName("POST /api/usuarios com dados inválidos deve retornar 400")
    void deveRetornarBadRequestAoCriarComDadosInvalidos() throws Exception {
        UsuarioRequestDTO invalido = new UsuarioRequestDTO("", "email-invalido");

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/usuarios deve retornar 200 OK")
    void deveListarUsuarios() throws Exception {
        when(usuarioService.listar()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/usuarios"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(usuarioId.toString()))
                .andExpect(jsonPath("$[0].nome").value("Ana Souza"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} deve retornar 200 OK")
    void deveBuscarPorId() throws Exception {
        when(usuarioService.buscarPorId(usuarioId)).thenReturn(response);

        mockMvc.perform(get("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(usuarioId.toString()))
                .andExpect(jsonPath("$.email").value("ana.souza@email.com"));
    }

    @Test
    @DisplayName("GET /api/usuarios/{id} inexistente deve retornar 404")
    void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
        when(usuarioService.buscarPorId(usuarioId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"));

        mockMvc.perform(get("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/usuarios/{id} deve retornar 200 OK")
    void deveAtualizarUsuario() throws Exception {
        UsuarioResponseDTO atualizado = new UsuarioResponseDTO(
                usuarioId,
                "Ana Silva",
                "ana.silva@email.com",
                response.getDataCriacao()
        );
        when(usuarioService.atualizar(eq(usuarioId), any(UsuarioRequestDTO.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/usuarios/{id}", usuarioId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new UsuarioRequestDTO("Ana Silva", "ana.silva@email.com"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Ana Silva"))
                .andExpect(jsonPath("$.email").value("ana.silva@email.com"));
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} deve retornar 204 No Content")
    void deveDeletarUsuario() throws Exception {
        doNothing().when(usuarioService).deletar(usuarioId);

        mockMvc.perform(delete("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/usuarios/{id} inexistente deve retornar 404")
    void deveRetornarNotFoundAoDeletarIdInexistente() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuário não encontrado"))
                .when(usuarioService).deletar(usuarioId);

        mockMvc.perform(delete("/api/usuarios/{id}", usuarioId))
                .andExpect(status().isNotFound());
    }
}
