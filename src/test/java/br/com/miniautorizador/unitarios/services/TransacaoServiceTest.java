package br.com.miniautorizador.unitarios.services;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.domain.cartao.exception.SaldoInsuficienteException;
import br.com.miniautorizador.domain.cartao.exception.SenhaInvalidaException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import br.com.miniautorizador.service.transacao.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para o serviço de transação de débito no cartão.
 * <p>
 * Essa classe é responsável por testar a lógica de negócios do serviço de transação de débito, garantindo que as operações sejam executadas corretamente e de acordo com as regras de negócios.
 * Os testes verificam se o serviço está funcionando corretamente na transação de débito no cartão, além de outras operações específicas do serviço.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class TransacaoServiceTest {
    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private TransacaoService transacaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transacaoService = new TransacaoService(cartaoRepository, passwordEncoder);
    }

    @DisplayName("Teste de realização de transação de débito com sucesso")
    @Test
    void testRealizarTransacao_ComSucesso() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "encoded_password", BigDecimal.valueOf(500.00));

        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(true);

        transacaoService.realizarTransacao(transacaoRequest);
        verify(cartaoRepository, times(1)).findByNumeroCartao(transacaoRequest.getNumeroCartao());
        verify(cartaoRepository, times(1)).save(cartaoMock);
    }

    @DisplayName("Teste de realização de transação de débito com cartão inexistente")
    @Test
    void testRealizarTransacao_CartaoInexistente() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.empty());

        assertThrows(CartaoInexistenteTransacaoException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com senha inválida")
    @Test
    void testRealizarTransacao_SenhaInvalida() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "encoded_password", BigDecimal.valueOf(500.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com saldo insuficiente")
    @Test
    void testRealizarTransacao_SaldoInsuficiente() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(600.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "1234", BigDecimal.valueOf(500.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(true);

        assertThrows(SaldoInsuficienteException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com concorrência de operação")
    @Test
    void testAtualizarSaldo_ConcorrenciaOperacao() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(300.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "1234", BigDecimal.valueOf(500.00));

        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(true);
        doThrow(OptimisticLockingFailureException.class).when(cartaoRepository).save(any());

        assertThrows(RuntimeException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com request null")
    @Test
    void testRealizarTransacao_RequestNull() {
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(null));
    }

    @DisplayName("Teste de realização de transação de débito com número de cartão nulo")
    @Test
    void testRealizarTransacao_NumeroCartaoNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest(null, "1234", BigDecimal.valueOf(100.00));
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com senha nula")
    @Test
    void testRealizarTransacao_SenhaNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", null, BigDecimal.valueOf(100.00));
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @DisplayName("Teste de realização de transação de débito com valor nulo")
    @Test
    void testRealizarTransacao_ValorNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", null);
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }
}
