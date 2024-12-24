package br.com.miniautorizador.presentation.controller;

import br.com.miniautorizador.application.transacao.RealizarTransacaoUseCase;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final RealizarTransacaoUseCase realizarTransacaoUseCase;

    @Autowired
    public TransacaoController(RealizarTransacaoUseCase realizarTransacaoUseCase) {
        this.realizarTransacaoUseCase = realizarTransacaoUseCase;
    }

    /**
     * Realiza uma transação de débito no cartão.
     *
     * @param transacaoRequest Dados da transação.
     * @return Resposta indicando o status da transação.
     */
    @PostMapping
    public ResponseEntity<String> realizarTransacao(@Valid @RequestBody TransacaoRequest transacaoRequest) {
        realizarTransacaoUseCase.realizarTransacao(transacaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
