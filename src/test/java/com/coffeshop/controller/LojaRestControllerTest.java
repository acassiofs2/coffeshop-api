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

import com.coffeshop.dto.LojaRequestDTO;
import com.coffeshop.dto.LojaResponseDTO;
import com.coffeshop.service.LojaService;
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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

@WebMvcTest(LojaRestController.class)
class LojaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private LojaService lojaService;

    private UUID lojaId;
    private UUID usuarioId;
    private LojaResponseDTO response;
    private LojaRequestDTO request;

    @BeforeEach
    void setUp() {
        lojaId = UUID.randomUUID();
        usuarioId = UUID.randomUUID();
        request = new LojaRequestDTO(
                "Coffee Shop Centro",
                "Rua das Flores, 100",
                "(11) 98765-4321",
                usuarioId
        );
        response = new LojaResponseDTO(
                lojaId,
                "Coffee Shop Centro",
                "Rua das Flores, 100",
                "(11) 98765-4321",
                usuarioId,
                LocalDateTime.of(2026, 1, 15, 10, 0)
        );
    }

    @Test
    @DisplayName("POST /api/lojas deve retornar 201 Created")
    void deveCriarLoja() throws Exception {
        when(lojaService.criar(any(LojaRequestDTO.class))).thenReturn(response);

        mockMvc.perform(post("/api/lojas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "http://localhost/api/lojas/" + lojaId))
                .andExpect(jsonPath("$.id").value(lojaId.toString()))
                .andExpect(jsonPath("$.nome").value("Coffee Shop Centro"))
                .andExpect(jsonPath("$.endereco").value("Rua das Flores, 100"))
                .andExpect(jsonPath("$.telefone").value("(11) 98765-4321"))
                .andExpect(jsonPath("$.usuarioId").value(usuarioId.toString()));
    }

    @Test
    @DisplayName("POST /api/lojas com dados inválidos deve retornar 400")
    void deveRetornarBadRequestAoCriarComDadosInvalidos() throws Exception {
        LojaRequestDTO invalido = new LojaRequestDTO("", "", "", null);

        mockMvc.perform(post("/api/lojas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalido)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("GET /api/lojas deve retornar 200 OK paginado")
    void deveListarLojas() throws Exception {
        when(lojaService.listar(any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1));

        mockMvc.perform(get("/api/lojas")
                        .param("page", "0")
                        .param("size", "20"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(lojaId.toString()))
                .andExpect(jsonPath("$.content[0].nome").value("Coffee Shop Centro"))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(20))
                .andExpect(jsonPath("$.number").value(0));
    }

    @Test
    @DisplayName("GET /api/lojas/{id} deve retornar 200 OK")
    void deveBuscarPorId() throws Exception {
        when(lojaService.buscarPorId(lojaId)).thenReturn(response);

        mockMvc.perform(get("/api/lojas/{id}", lojaId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(lojaId.toString()))
                .andExpect(jsonPath("$.endereco").value("Rua das Flores, 100"));
    }

    @Test
    @DisplayName("GET /api/lojas/{id} inexistente deve retornar 404")
    void deveRetornarNotFoundAoBuscarIdInexistente() throws Exception {
        when(lojaService.buscarPorId(lojaId))
                .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"));

        mockMvc.perform(get("/api/lojas/{id}", lojaId))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/lojas/{id} deve retornar 200 OK")
    void deveAtualizarLoja() throws Exception {
        LojaResponseDTO atualizado = new LojaResponseDTO(
                lojaId,
                "Coffee Shop Jardins",
                "Av. Paulista, 500",
                "(11) 91234-5678",
                usuarioId,
                response.getDataCriacao()
        );
        when(lojaService.atualizar(eq(lojaId), any(LojaRequestDTO.class))).thenReturn(atualizado);

        mockMvc.perform(put("/api/lojas/{id}", lojaId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                new LojaRequestDTO(
                                        "Coffee Shop Jardins",
                                        "Av. Paulista, 500",
                                        "(11) 91234-5678",
                                        usuarioId))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Coffee Shop Jardins"))
                .andExpect(jsonPath("$.endereco").value("Av. Paulista, 500"))
                .andExpect(jsonPath("$.telefone").value("(11) 91234-5678"));
    }

    @Test
    @DisplayName("DELETE /api/lojas/{id} deve retornar 204 No Content")
    void deveDeletarLoja() throws Exception {
        doNothing().when(lojaService).deletar(lojaId);

        mockMvc.perform(delete("/api/lojas/{id}", lojaId))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("DELETE /api/lojas/{id} inexistente deve retornar 404")
    void deveRetornarNotFoundAoDeletarIdInexistente() throws Exception {
        doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Loja não encontrada"))
                .when(lojaService).deletar(lojaId);

        mockMvc.perform(delete("/api/lojas/{id}", lojaId))
                .andExpect(status().isNotFound());
    }
}
