package br.com.miniautorizador.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartaoRequest {
    @NotNull
    @Size(min = 16, max = 16)
    private String numeroCartao;

    @NotNull
    @Size(min = 4, max = 4)
    private String senha;
}
