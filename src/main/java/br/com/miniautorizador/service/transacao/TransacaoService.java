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
import java.util.Objects;


/**
 * Serviço responsável por gerenciar transações de débito do cartão.
 * <p>
 * Essa classe é responsável por realizar operações de transação de débito do cartão.
 * Além disso, ela também é responsável por garantir a integridade das transações e aplicar as regras de negócios relacionadas a transações.
 *
 * @author Fabiana Costa
 */
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
        validarRequest(transacaoRequest);
        Cartao cartao = buscarCartao(transacaoRequest.getNumeroCartao());
        validarSenha(cartao, transacaoRequest.getSenhaCartao());
        validarSaldo(cartao, transacaoRequest.getValor());
        atualizarSaldo(cartao, transacaoRequest.getValor());
    }

    private void validarRequest(TransacaoRequest transacaoRequest) {
        Objects.requireNonNull(transacaoRequest, "Request não pode ser nula.");
        Objects.requireNonNull(transacaoRequest.getNumeroCartao(), "O número do cartão deve ser informado.");
        Objects.requireNonNull(transacaoRequest.getSenhaCartao(), "A senha do cartão deve ser informada.");
        Objects.requireNonNull(transacaoRequest.getValor(), "O valor a ser debitado deve ser informado.");
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
