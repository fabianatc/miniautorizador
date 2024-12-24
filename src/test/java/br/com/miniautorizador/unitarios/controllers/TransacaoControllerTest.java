package br.com.miniautorizador.unitarios.controllers;

import br.com.miniautorizador.application.transacao.RealizarTransacaoUseCase;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.presentation.controller.TransacaoController;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

@ExtendWith(MockitoExtension.class)
class TransacaoControllerTest {
    @Mock
    private RealizarTransacaoUseCase realizarTransacaoUseCase;

    private TransacaoController transacaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        transacaoController = new TransacaoController(realizarTransacaoUseCase);
    }

    @Test
    void testCriarTransacao_ComSucesso() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));

        doNothing().when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        ResponseEntity<String> response = transacaoController.realizarTransacao(transacaoRequest);
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("OK");

        assertEquals(expectedResponse, response);
    }

    @Test
    void testCriarCartao_CartaoInexistente() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        doThrow(new CartaoInexistenteTransacaoException(transacaoRequest.getNumeroCartao())).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        CartaoInexistenteTransacaoException exception = assertThrows(CartaoInexistenteTransacaoException.class, () -> transacaoController.realizarTransacao(transacaoRequest));
        assertThat(exception.getMessage()).isEqualTo("O cartão [" + transacaoRequest.getNumeroCartao() + "] não foi encontrado.");
    }


    @Test
    void testCriarTransacao_TransacaoRequestNull() {
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(null);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(null)
        );

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testCriarTransacao_NumeroCartaoNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest(null, "1234", BigDecimal.valueOf(100.00));
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testCriarTransacao_SenhaNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", null, BigDecimal.valueOf(100.00));
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        assertThat(exception.getMessage()).isNull();
    }

    @Test
    void testCriarTransacao_ValorNull() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", null);
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        assertThat(exception.getMessage()).isNull();
    }
}
