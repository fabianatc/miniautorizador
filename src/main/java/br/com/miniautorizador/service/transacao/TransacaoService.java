package br.com.miniautorizador.service.transacao;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.domain.cartao.exception.SaldoInsuficienteException;
import br.com.miniautorizador.domain.cartao.exception.SenhaInvalidaException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class TransacaoService {
    private final CartaoRepository cartaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public TransacaoService(
            CartaoRepository cartaoRepository,
            PasswordEncoder passwordEncoder) {
        this.cartaoRepository = cartaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public void realizarTransacao(TransacaoRequest transacaoRequest) {
        Cartao cartao = buscarCartao(transacaoRequest.getNumeroCartao());
        validarSenha(cartao, transacaoRequest.getSenhaCartao());
        validarSaldo(cartao, transacaoRequest.getValor());
        atualizarSaldo(cartao, transacaoRequest.getValor());
    }

    private Cartao buscarCartao(String numeroCartao) {
        return cartaoRepository.findByNumeroCartao(numeroCartao)
                .orElseThrow(() -> new CartaoInexistenteTransacaoException(numeroCartao));
    }

    private void validarSenha(Cartao cartao, String senhaCartao) {
        if (!passwordEncoder.matches(senhaCartao, cartao.getSenha())) {
            throw new SenhaInvalidaException();
        }
    }

    private void validarSaldo(Cartao cartao, BigDecimal valor) {
        if (cartao.getSaldo().compareTo(valor) < 0) {
            throw new SaldoInsuficienteException();
        }
    }

    private void atualizarSaldo(Cartao cartao, BigDecimal valor) {
        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        try {
            cartaoRepository.save(cartao);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Conflito de concorrência ao atualizar o saldo do cartão.", e);
        }
    }
}
