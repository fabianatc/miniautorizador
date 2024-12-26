package br.com.miniautorizador.presentation.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class CartaoRequest {
    @NotBlank(message = "O número do cartão é obrigatório.")
    @NotNull(message = "O número do cartão é obrigatório.")
    @Size(min = 16, max = 16, message = "O número do cartão deve ter 16 dígitos.")
    @Pattern(regexp = "\\d{16}", message = "O número do cartão deve conter apenas dígitos.")
    private String numeroCartao;

    @NotBlank(message = "A senha do cartão é obrigatória.")
    @NotNull(message = "A senha do cartão é obrigatória.")
    @Size(min = 4, max = 4, message = "A senha do cartão deve ter 4 dígitos.")
    @Pattern(regexp = "\\d{4}", message = "A senha do cartão deve conter apenas dígitos.")
    private String senha;
}
