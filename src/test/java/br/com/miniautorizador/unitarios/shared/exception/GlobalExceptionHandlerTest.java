package br.com.miniautorizador.unitarios.shared.exception;

import br.com.miniautorizador.domain.cartao.exception.*;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import br.com.miniautorizador.shared.exception.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Teste de classe para GlobalExceptionHandler.
 * <p>
 * Esta classe contem casos de teste para verificar a funcionalidade do GlobalExceptionHandler.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler globalExceptionHandler;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @DisplayName("Teste de handle para CartaoExistenteException")
    @Test
    void testHandle_CartaoExistenteException() {
        CartaoExistenteException exception = new CartaoExistenteException("1234", "1234567890123456");
        ResponseEntity<CartaoResponse> response = globalExceptionHandler.handleCartaoExistenteException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("1234567890123456", Objects.requireNonNull(response.getBody()).getNumeroCartao());
        assertEquals("1234", response.getBody().getSenha());
    }

    @DisplayName("Teste de handle para CartaoInexistenteException")
    @Test
    void testHandle_CartaoInexistenteException() {
        CartaoInexistenteException exception = new CartaoInexistenteException("1234567890123456");
        ResponseEntity<Void> response = globalExceptionHandler.handleCartaoInexistenteException(exception);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @DisplayName("Teste de handle para CartaoInexistenteTransacaoException")
    @Test
    void testHandle_CartaoInexistenteTransacaoException() {
        CartaoInexistenteTransacaoException exception = new CartaoInexistenteTransacaoException("1234567890123456");
        ResponseEntity<String> response = globalExceptionHandler.handleCartaoInexistenteTransacaoException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("CARTAO_INEXISTENTE", response.getBody());
    }

    @DisplayName("Teste de handle para SenhaInvalidaException")
    @Test
    void testHandle_SenhaInvalidaException() {
        SenhaInvalidaException exception = new SenhaInvalidaException();
        ResponseEntity<String> response = globalExceptionHandler.handleSenhaInvalidaException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("SENHA_INVALIDA", response.getBody());
    }

    @DisplayName("Teste de handle para SaldoInsuficienteException")
    @Test
    void testHandle_SaldoInsuficienteException() {
        SaldoInsuficienteException exception = new SaldoInsuficienteException();
        ResponseEntity<String> response = globalExceptionHandler.handleSaldoInsuficienteException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("SALDO_INSUFICIENTE", response.getBody());
    }

    @DisplayName("Teste de handle para OptimisticLockingFailureException")
    @Test
    void testHandle_OptimisticLockingFailureException() {
        OptimisticLockingFailureException exception = new OptimisticLockingFailureException("Conflito de concorrência");
        ResponseEntity<String> response = globalExceptionHandler.handleOptimisticLockingFailure(exception);

        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        assertEquals("CONFLITO_DE_CONCORRENCIA", response.getBody());
    }

    @DisplayName("Teste de handle para DataIntegrityViolationException")
    @Test
    void testHandle_DataIntegrityViolationException() {
        DataIntegrityViolationException exception = new DataIntegrityViolationException("Violação de integridade de dados");
        ResponseEntity<String> response = globalExceptionHandler.handleDataIntegrityViolationException(exception);

        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, response.getStatusCode());
        assertEquals("DADOS_INVALIDOS", response.getBody());
    }

    @DisplayName("Teste de handle para HttpMessageNotReadableException")
    @Test
    void testHandle_HttpMessageNotReadableException() {
        HttpMessageNotReadableException exception = new HttpMessageNotReadableException("Request inválida", null, null);
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("REQUEST_INVALIDA", response.getBody());
    }

    @DisplayName("Teste de handle para Exception")
    @Test
    void testHandle_GenericException() {
        Exception exception = new Exception("Erro interno do servidor");
        ResponseEntity<String> response = globalExceptionHandler.handleGenericException(exception);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        assertEquals("ERRO_INTERNO", response.getBody());
    }
}

