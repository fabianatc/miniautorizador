package br.com.miniautorizador.presentation.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartaoRequest {
    @NotNull
    @Size(min = 16, max = 16)
    @Pattern(regexp = "\\d{16}", message = "O número do cartão deve conter apenas dígitos.")
    private String numeroCartao;

    @NotNull
    @Size(min = 4, max = 4)
    @Pattern(regexp = "\\d{4}", message = "A senha do cartão deve conter apenas dígitos.")
    private String senha;
}
