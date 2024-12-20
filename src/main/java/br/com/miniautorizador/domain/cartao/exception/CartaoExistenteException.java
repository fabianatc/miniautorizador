package br.com.miniautorizador.domain.cartao.exception;

import lombok.Getter;

@Getter
public class CartaoExistenteException extends RuntimeException {
    private String senha;
    private String numeroCartao;

    public CartaoExistenteException(String senha, String numeroCartao) {
        super("O cartão [" + numeroCartao + "] já existe.");
        this.senha = senha;
        this.numeroCartao = numeroCartao;
    }
}
