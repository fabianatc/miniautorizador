package br.com.miniautorizador.presentation.controller;

import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import br.com.miniautorizador.service.transacao.TransacaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final TransacaoService transacaoService;

    @Autowired
    public TransacaoController(TransacaoService transacaoService) {
        this.transacaoService = transacaoService;
    }

    /**
     * Realiza uma transação de débito no cartão.
     *
     * @param transacaoRequest Dados da transação.
     * @return Resposta indicando o status da transação.
     */
    @PostMapping
    public ResponseEntity<String> realizarTransacao(@Validated @RequestBody TransacaoRequest transacaoRequest) {
        transacaoService.realizarTransacao(transacaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
