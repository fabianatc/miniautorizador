package br.com.miniautorizador.presentation.controller;

import br.com.miniautorizador.application.cartao.CriarCartaoUseCase;
import br.com.miniautorizador.application.cartao.ObterSaldoUseCase;
import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    private final CriarCartaoUseCase criarCartaoUseCase;
    private final ObterSaldoUseCase obterSaldoUseCase;

    @Autowired
    public CartaoController(
            ObterSaldoUseCase obterSaldoUseCase,
            CriarCartaoUseCase criarCartaoUseCase
    ) {
        this.criarCartaoUseCase = criarCartaoUseCase;
        this.obterSaldoUseCase = obterSaldoUseCase;
    }

    @PostMapping
    public ResponseEntity<CartaoResponse> criarCartao(@Valid @RequestBody CartaoRequest cartaoRequest) {
        Cartao cartao = criarCartaoUseCase.criarCartao(cartaoRequest);
        CartaoResponse response = new CartaoResponse(cartao.getNumeroCartao(), cartaoRequest.getSenha());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{numeroCartao}")
    public ResponseEntity<BigDecimal> obterSaldo(@PathVariable String numeroCartao) {
        BigDecimal saldo = obterSaldoUseCase.obterSaldo(numeroCartao);
        return ResponseEntity.ok(saldo);
    }
}
