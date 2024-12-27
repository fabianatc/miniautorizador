package br.com.miniautorizador.unitarios.application.transacao;

import br.com.miniautorizador.application.transacao.RealizarTransacaoUseCaseImpl;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.domain.cartao.exception.SaldoInsuficienteException;
import br.com.miniautorizador.domain.cartao.exception.SenhaInvalidaException;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import br.com.miniautorizador.service.transacao.TransacaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Classe de teste para a implementação do caso de uso de realizar transação.
 * <p>
 * Esta classe contém testes para verificar a funcionalidade da classe RealizarTransacaoUseCaseImpl,
 * garantindo que as transações sejam processadas corretamente e que os resultados sejam consistentes.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class RealizarTransacaoUseCaseImplTest {
    @Mock
    private TransacaoService transacaoService;

    @InjectMocks
    private RealizarTransacaoUseCaseImpl realizarTransacaoUseCaseImpl;

    private TransacaoRequest transacaoRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        realizarTransacaoUseCaseImpl = new RealizarTransacaoUseCaseImpl(transacaoService);

        transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
    }

    @DisplayName("Teste de realização de transação de débito com sucesso")
    @Test
    void testRealizarTransacao_Sucesso() {
        doNothing().when(transacaoService).realizarTransacao(transacaoRequest);

        realizarTransacaoUseCaseImpl.realizarTransacao(transacaoRequest);

        verify(transacaoService, times(1)).realizarTransacao(transacaoRequest);
    }

    @DisplayName("Teste de realização de transação de débito com cartão inexistente")
    @Test
    void testRealizarTransacao_CartaoInexistente() {
        doThrow(new CartaoInexistenteTransacaoException(transacaoRequest.getNumeroCartao())).when(transacaoService).realizarTransacao(transacaoRequest);

        CartaoInexistenteTransacaoException exception = assertThrows(CartaoInexistenteTransacaoException.class, () ->
                realizarTransacaoUseCaseImpl.realizarTransacao(transacaoRequest));

        assertEquals("O cartão [" + transacaoRequest.getNumeroCartao() + "] não foi encontrado.", exception.getMessage());

        verify(transacaoService, times(1)).realizarTransacao(transacaoRequest);
    }

    @DisplayName("Teste de realização de transação de débito com senha inválida")
    @Test
    void testRealizarTransacao_SenhaInvalida() {
        doThrow(new SenhaInvalidaException()).when(transacaoService).realizarTransacao(transacaoRequest);

        SenhaInvalidaException exception = assertThrows(SenhaInvalidaException.class, () ->
                realizarTransacaoUseCaseImpl.realizarTransacao(transacaoRequest));

        assertEquals("Senha inválida.", exception.getMessage());

        verify(transacaoService, times(1)).realizarTransacao(transacaoRequest);
    }

    @DisplayName("Teste de realização de transação de débito com saldo insuficiente")
    @Test
    void testRealizarTransacao_SaldoInsuficiente() {
        doThrow(new SaldoInsuficienteException()).when(transacaoService).realizarTransacao(transacaoRequest);

        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () ->
                realizarTransacaoUseCaseImpl.realizarTransacao(transacaoRequest));

        assertEquals("Saldo insuficiente no cartão.", exception.getMessage());

        verify(transacaoService, times(1)).realizarTransacao(transacaoRequest);
    }
}
