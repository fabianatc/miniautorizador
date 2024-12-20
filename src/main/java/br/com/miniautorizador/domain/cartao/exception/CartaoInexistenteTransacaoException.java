package br.com.miniautorizador.domain.cartao.exception;

import lombok.Getter;

@Getter
public class CartaoInexistenteTransacaoException extends RuntimeException {
    private String numeroCartao;

    public CartaoInexistenteTransacaoException(String numeroCartao) {
        super("O cartão [" + numeroCartao + "] não foi encontrado.");
        this.numeroCartao = numeroCartao;
    }
}
