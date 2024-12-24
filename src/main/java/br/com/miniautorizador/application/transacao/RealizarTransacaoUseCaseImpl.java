package br.com.miniautorizador.application.transacao;

import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import br.com.miniautorizador.service.transacao.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RealizarTransacaoUseCaseImpl implements RealizarTransacaoUseCase {
    private final TransacaoService transacaoService;

    @Autowired
    public RealizarTransacaoUseCaseImpl(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    @Override
    public void realizarTransacao(TransacaoRequest transacaoRequest) {
        transacaoService.realizarTransacao(transacaoRequest);
    }
}
