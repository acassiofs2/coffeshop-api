package com.coffeshop.dto;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UsuarioPatchDTO {

    private String nome;

    @Email(message = "O e-mail deve ser válido")
    private String email;
}
