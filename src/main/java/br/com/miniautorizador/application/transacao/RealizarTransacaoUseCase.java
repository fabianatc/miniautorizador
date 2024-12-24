package br.com.miniautorizador.application.transacao;

import br.com.miniautorizador.presentation.dto.TransacaoRequest;

public interface RealizarTransacaoUseCase {
    void realizarTransacao(TransacaoRequest transacaoRequest);
}
