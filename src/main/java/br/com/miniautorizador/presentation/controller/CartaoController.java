package br.com.miniautorizador.presentation.controller;

import br.com.miniautorizador.application.cartao.CriarCartaoUseCase;
import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cartoes")
public class CartaoController {
    private final CriarCartaoUseCase criarCartaoUseCase;

    @Autowired
    public CartaoController(CriarCartaoUseCase criarCartaoUseCase) {
        this.criarCartaoUseCase = criarCartaoUseCase;
    }

    @PostMapping
    public ResponseEntity<CartaoResponse> criarCartao(@Valid @RequestBody CartaoRequest cartaoRequest) {
        Cartao cartao = criarCartaoUseCase.criarCartao(cartaoRequest);
        CartaoResponse response = new CartaoResponse(cartao.getNumeroCartao(), cartaoRequest.getSenha());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
