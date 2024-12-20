package br.com.miniautorizador.application.cartao;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class ObterSaldoUseCaseImpl implements ObterSaldoUseCase {
    private final CartaoRepository cartaoRepository;

    @Autowired
    public ObterSaldoUseCaseImpl(CartaoRepository cartaoRepository) {
        this.cartaoRepository = cartaoRepository;
    }

    @Override
    public BigDecimal obterSaldo(String numeroCartao) {
        Cartao cartao = cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new CartaoInexistenteException(numeroCartao));
        return cartao.getSaldo();
    }
}
