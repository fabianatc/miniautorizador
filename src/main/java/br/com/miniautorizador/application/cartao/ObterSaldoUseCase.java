package br.com.miniautorizador.application.cartao;

import java.math.BigDecimal;

public interface ObterSaldoUseCase {
    BigDecimal obterSaldo(String numeroCartao);
}
