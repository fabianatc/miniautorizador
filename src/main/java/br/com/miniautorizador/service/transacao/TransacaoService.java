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
import java.util.Optional;

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

    /**
     * Realiza uma transação de débito no cartão
     *
     * @param transacaoRequest Dados da transação.
     */
    @Transactional
    public void realizarTransacao(TransacaoRequest transacaoRequest) {
        cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())
                .map(cartao -> validarSenha(cartao, transacaoRequest))
                .map(cartao -> validarSaldo(cartao, transacaoRequest))
                .map(cartao -> atualizarSaldo(cartao, transacaoRequest.getValor()))
                .orElseThrow(() -> new CartaoInexistenteTransacaoException(transacaoRequest.getNumeroCartao()));
    }

    /**
     * Valida a senha do cartão.
     *
     * @param cartao O cartão encontrado.
     * @return O próprio cartão se a senha for válida.
     * @throws SenhaInvalidaException Se a senha for inválida.
     */
    private Cartao validarSenha(Cartao cartao, TransacaoRequest transacaoRequest) {
        Optional.of(cartao)
                .filter(c -> passwordEncoder.matches(transacaoRequest.getSenhaCartao(), c.getSenha()))
                .orElseThrow(SenhaInvalidaException::new);
        return cartao;
    }

    /**
     * Valida se o saldo do cartão é suficiente para a transação.
     *
     * @param cartao O cartão com a senha validada.
     * @return O próprio cartão se o saldo for suficiente.
     * @throws SaldoInsuficienteException Se o saldo for insuficiente.
     */
    private Cartao validarSaldo(Cartao cartao, TransacaoRequest transacaoRequest) {
        Optional.of(cartao)
                .filter(c -> c.getSaldo().compareTo(transacaoRequest.getValor()) >= 0)
                .orElseThrow(SaldoInsuficienteException::new);
        return cartao;
    }

    /**
     * Atualiza o saldo do cartão após a transação.
     *
     * @param cartao O cartão com saldo suficiente.
     * @param valor  O valor da transação.
     * @return O cartão atualizado.
     * @throws OptimisticLockingFailureException Em caso de conflito de concorrência.
     */
    private Cartao atualizarSaldo(Cartao cartao, BigDecimal valor) {
        cartao.setSaldo(cartao.getSaldo().subtract(valor));
        try {
            return cartaoRepository.save(cartao);
        } catch (OptimisticLockingFailureException e) {
            throw new RuntimeException("Conflito de concorrência ao atualizar o saldo do cartão.", e);
        }
    }
}
