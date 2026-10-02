package com.coffeshop.dto;

import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LojaPatchDTO {

    private String nome;
    private String endereco;
    private String telefone;
    private UUID usuarioId;
}
