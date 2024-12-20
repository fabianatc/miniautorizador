package br.com.miniautorizador.domain.cartao.exception;

public class CartaoInexistenteException extends RuntimeException {
    public CartaoInexistenteException(String numeroCartao) {
        super("O cartão [" + numeroCartao + "] não foi encontrado.");
    }
}
