package br.com.miniautorizador.domain.cartao.exception;

import lombok.Getter;

@Getter
public class CartaoInexistenteException extends RuntimeException {
    private String numeroCartao;

    public CartaoInexistenteException(String numeroCartao) {
        super("O cartão [" + numeroCartao + "] não foi encontrado.");
        this.numeroCartao = numeroCartao;
    }
}
