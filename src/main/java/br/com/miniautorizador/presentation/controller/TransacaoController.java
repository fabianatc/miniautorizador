package br.com.miniautorizador.presentation.controller;

import br.com.miniautorizador.application.transacao.RealizarTransacaoUseCase;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Transações", description = "Operações relacionadas a transações de débito dos cartões.")
@RestController
@RequestMapping("/transacoes")
public class TransacaoController {
    private final RealizarTransacaoUseCase realizarTransacaoUseCase;

    @Autowired
    public TransacaoController(RealizarTransacaoUseCase realizarTransacaoUseCase) {
        this.realizarTransacaoUseCase = realizarTransacaoUseCase;
    }

    @Operation(
            summary = "Transação de débito",
            description = "Realiza uma transação de débito de saldo no cartão."
    )
    @PostMapping
    public ResponseEntity<String> realizarTransacao(@Valid @RequestBody TransacaoRequest transacaoRequest) {
        realizarTransacaoUseCase.realizarTransacao(transacaoRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body("OK");
    }
}
