package br.com.miniautorizador.unitarios.presentation.controller;

import br.com.miniautorizador.application.transacao.RealizarTransacaoUseCase;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteTransacaoException;
import br.com.miniautorizador.domain.cartao.exception.SaldoInsuficienteException;
import br.com.miniautorizador.domain.cartao.exception.SenhaInvalidaException;
import br.com.miniautorizador.presentation.controller.TransacaoController;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

/**
 * Classe de teste para o controller de transações.
 * <p>
 * Essa classe é responsável por testar a funcionalidade do controller de transações para débito do cartão.
 *
 * @author Fabiana Costa
 */
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

    @DisplayName("Teste de execução de transação de débito com sucesso")
    @Test
    void testExecutarTransacao_ComSucesso() {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));

        // Realiza a transação sem lançar exceções
        doNothing().when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Verifica se a resposta HTTP é 201 Created
        ResponseEntity<String> response = transacaoController.realizarTransacao(transacaoRequest);
        ResponseEntity<String> expectedResponse = ResponseEntity.status(HttpStatus.CREATED).body("OK");

        assertEquals(expectedResponse, response);
    }

    @DisplayName("Teste de execução de transação de débito com cartão inexistente")
    @Test
    void testExecutarCartao_CartaoInexistente() {
        // Arrange: Cria uma requisição de transação com dados de cartão inexistente
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));

        // Simula o comportamento do caso de uso, lançando exceção para cartão inexistente
        doThrow(new CartaoInexistenteTransacaoException(transacaoRequest.getNumeroCartao()))
                .when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Act & Assert: Verifica se a exceção CartaoInexistenteTransacaoException é lançada e a mensagem está correta
        CartaoInexistenteTransacaoException exception = assertThrows(CartaoInexistenteTransacaoException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest));

        assertThat(exception.getMessage()).isEqualTo("O cartão [" + transacaoRequest.getNumeroCartao() + "] não foi encontrado.");
    }


    @DisplayName("Teste de execução de transação de débito com request nula")
    @Test
    void testExecutarTransacao_TransacaoRequestNull() {
        // Arrange & Act: Simula o comportamento do caso de uso, lançando exceção para request nulo
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(null);

        // Assert: Verifica se a exceção NullPointerException é lançada
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(null)
        );

        // Verifica se a mensagem da exceção é nula
        assertThat(exception.getMessage()).isNull();
    }

    @DisplayName("Teste de execução de transação de débito com número de cartão nulo")
    @Test
    void testExecutarTransacao_NumeroCartaoNull() {
        // Arrange: Cria uma requisição de transação com número de cartão nulo
        TransacaoRequest transacaoRequest = new TransacaoRequest(null, "1234", BigDecimal.valueOf(100.00));

        // Act & Assert: Verifica se a exceção NullPointerException é lançada e a mensagem está correta
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        // Verifica se a mensagem da exceção é nula
        assertThat(exception.getMessage()).isNull();
    }

    @DisplayName("Teste de execução de transação de débito com senha nula")
    @Test
    void testExecutarTransacao_SenhaNull() {
        // Arrange: Cria uma requisição de transação com senha nula
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", null, BigDecimal.valueOf(100.00));

        // Simula o comportamento do caso de uso, lançando exceção para senha nula
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Act & Assert: Verifica se a exceção NullPointerException é lançada e a mensagem está correta
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        // Verifica se a mensagem da exceção é nula
        assertThat(exception.getMessage()).isNull();
    }

    @DisplayName("Teste de execução de transação de débito com valor nulo")
    @Test
    void testExecutarTransacao_ValorNull() {
        // Arrange: Cria uma requisição de transação com valor nulo
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", null);

        // Simula o comportamento do caso de uso, lançando exceção para valor nulo
        doThrow(NullPointerException.class).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Act & Assert: Verifica se a exceção NullPointerException é lançada e a mensagem está correta
        NullPointerException exception = assertThrows(NullPointerException.class,
                () -> transacaoController.realizarTransacao(transacaoRequest)
        );

        // Verifica se a mensagem da exceção é nula
        assertThat(exception.getMessage()).isNull();
    }

    @DisplayName("Teste de execução de transação de débito com senha de cartão inválida")
    @Test
    void testExecutarTransacao_SenhaInvalida() {
        // Arrange: Cria uma requisição de transação com senha de cartão inválida
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));

        // Simula o comportamento do caso de uso, lançando exceção para senha de cartão inválida
        doThrow(new SenhaInvalidaException()).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Act & Assert: Verifica se a exceção SenhaInvalidaException é lançada e a mensagem está correta
        SenhaInvalidaException exception = assertThrows(SenhaInvalidaException.class, () -> transacaoController.realizarTransacao(transacaoRequest));
        assertThat(exception.getMessage()).isEqualTo("Senha inválida.");
    }

    @DisplayName("Teste de execução de transação de débito com saldo insuficiente")
    @Test
    void testExecutarTransacao_SaldoInsuficiente() {
        // Arrange: Cria uma requisição de transação com saldo insuficiente
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));

        // Simula o comportamento do caso de uso, lançando exceção para saldo insuficiente
        doThrow(new SaldoInsuficienteException()).when(realizarTransacaoUseCase).realizarTransacao(transacaoRequest);

        // Act & Assert: Verifica se a exceção SaldoInsuficienteException é lançada e a mensagem está correta
        SaldoInsuficienteException exception = assertThrows(SaldoInsuficienteException.class, () -> transacaoController.realizarTransacao(transacaoRequest));
        assertThat(exception.getMessage()).isEqualTo("Saldo insuficiente no cartão.");
    }
}
