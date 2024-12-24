package br.com.miniautorizador.unitarios.services;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.domain.cartao.exception.SaldoInsuficienteException;
import br.com.miniautorizador.domain.cartao.exception.SenhaInvalidaException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import br.com.miniautorizador.service.transacao.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    void testRealizarTransacao_CartaoInexistente() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.empty());

        assertThrows(CartaoInexistenteTransacaoException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testRealizarTransacao_SenhaInvalida() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "encoded_password", BigDecimal.valueOf(500.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(false);

        assertThrows(SenhaInvalidaException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testRealizarTransacao_SaldoInsuficiente() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(600.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "1234", BigDecimal.valueOf(500.00));
        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(true);

        assertThrows(SaldoInsuficienteException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testAtualizarSaldo_ConcorrenciaOperacao() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(300.00));
        Cartao cartaoMock = new Cartao(transacaoRequest.getNumeroCartao(), "1234", BigDecimal.valueOf(500.00));

        when(cartaoRepository.findByNumeroCartao(transacaoRequest.getNumeroCartao())).thenReturn(Optional.of(cartaoMock));
        when(passwordEncoder.matches(transacaoRequest.getSenhaCartao(), cartaoMock.getSenha())).thenReturn(true);
        doThrow(OptimisticLockingFailureException.class).when(cartaoRepository).save(any());

        assertThrows(RuntimeException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testRealizarTransacao_RequestNull() {
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(null));
    }

    @Test
    void testRealizarTransacao_NumeroCartaoNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest(null, "1234", BigDecimal.valueOf(100.00));
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testRealizarTransacao_SenhaNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", null, BigDecimal.valueOf(100.00));
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }

    @Test
    void testRealizarTransacao_ValorNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", null);
        assertThrows(NullPointerException.class, () -> transacaoService.realizarTransacao(transacaoRequest));
    }
}
