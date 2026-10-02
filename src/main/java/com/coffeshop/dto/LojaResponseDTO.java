package com.coffeshop.dto;

import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LojaResponseDTO {

    private UUID id;
    private String nome;
    private String endereco;
    private String telefone;
    private UUID usuarioId;
    private LocalDateTime dataCriacao;
}
