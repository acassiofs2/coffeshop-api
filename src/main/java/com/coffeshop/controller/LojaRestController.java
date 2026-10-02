package com.coffeshop.controller;

import com.coffeshop.dto.LojaPatchDTO;
import com.coffeshop.dto.LojaRequestDTO;
import com.coffeshop.dto.LojaResponseDTO;
import com.coffeshop.service.LojaService;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequestMapping("/api/lojas")
@RequiredArgsConstructor
public class LojaRestController {

    private final LojaService lojaService;

    @PostMapping
    public ResponseEntity<LojaResponseDTO> criar(@Valid @RequestBody LojaRequestDTO request) {
        LojaResponseDTO criada = lojaService.criar(request);
        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(criada.getId())
                .toUri();
        return ResponseEntity.created(location).body(criada);
    }

    @GetMapping
    public ResponseEntity<Page<LojaResponseDTO>> listar(@PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(lojaService.listar(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<LojaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(lojaService.buscarPorId(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<LojaResponseDTO> atualizar(
            @PathVariable UUID id,
            @Valid @RequestBody LojaRequestDTO request) {
        return ResponseEntity.ok(lojaService.atualizar(id, request));
    }

    @PatchMapping("/{id}")
    public ResponseEntity<LojaResponseDTO> atualizarParcial(
            @PathVariable UUID id,
            @Valid @RequestBody LojaPatchDTO request) {
        return ResponseEntity.ok(lojaService.atualizarParcial(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        lojaService.deletar(id);
        return ResponseEntity.noContent().build();
    }
}
