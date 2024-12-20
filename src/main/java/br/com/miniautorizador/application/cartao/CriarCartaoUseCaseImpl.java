package br.com.miniautorizador.application.cartao;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.service.CartaoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CriarCartaoUseCaseImpl implements CriarCartaoUseCase {
    private final CartaoService cartaoService;

    @Autowired
    public CriarCartaoUseCaseImpl(CartaoService cartaoService) {
        this.cartaoService = cartaoService;
    }

    @Override
    public Cartao criarCartao(CartaoRequest cartaoRequest) {
        return cartaoService.criarCartao(cartaoRequest);
    }
}

