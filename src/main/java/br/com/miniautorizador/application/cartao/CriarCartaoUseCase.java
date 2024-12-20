package br.com.miniautorizador.application.cartao;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.presentation.dto.CartaoRequest;

public interface CriarCartaoUseCase {
    Cartao criarCartao(CartaoRequest cartaoRequest);
}
