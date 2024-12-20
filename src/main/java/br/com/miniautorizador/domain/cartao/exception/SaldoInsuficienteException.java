package br.com.miniautorizador.domain.cartao.exception;

public class SaldoInsuficienteException extends RuntimeException {
    public SaldoInsuficienteException() {
        super("Saldo insuficiente para transação.");
    }
}