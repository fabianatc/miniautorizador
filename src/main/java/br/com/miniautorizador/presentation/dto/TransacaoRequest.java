package br.com.miniautorizador.presentation.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TransacaoRequest {
    @NotBlank(message = "O número do cartão é obrigatório.")
    @NotNull
    @Size(min = 16, max = 16, message = "O número do cartão deve ter 16 dígitos.")
    @Pattern(regexp = "\\d{16}", message = "O número do cartão deve conter apenas dígitos.")
    private String numeroCartao;

    @NotBlank(message = "A senha do cartão é obrigatória.")
    @NotNull
    @Size(min = 4, max = 4, message = "A senha do cartão deve ter 4 dígitos.")
    @Pattern(regexp = "\\d{4}", message = "A senha do cartão deve conter apenas dígitos.")
    private String senhaCartao;

    @NotNull
    @DecimalMin(value = "0.01", inclusive = true, message = "O valor da transação deve ser maior que 0.")
    private BigDecimal valor;
}
