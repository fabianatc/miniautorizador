package br.com.miniautorizador.presentation.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CartaoResponse {
    private String numeroCartao;
    private String senha;
}