package br.com.miniautorizador.unitarios.presentation.controller;

import br.com.miniautorizador.application.cartao.CriarCartaoUseCase;
import br.com.miniautorizador.application.cartao.ObterSaldoUseCase;
import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteException;
import br.com.miniautorizador.presentation.controller.CartaoController;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
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
import java.util.Objects;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

/**
 * Classe de teste para a controller de cartões.
 * <p>
 * Essa classe é responsável por testar a funcionalidade da controller de cartões, incluindo a criação e consulta de saldo de cartões.
 * Os testes verificam se a controller está funcionando corretamente e se está retornando as respostas esperadas.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class CartaoControllerTest {
    @Mock
    private CriarCartaoUseCase criarCartaoUseCase;

    @Mock
    private ObterSaldoUseCase obterSaldoUseCase;

    private CartaoController cartaoController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartaoController = new CartaoController(obterSaldoUseCase, criarCartaoUseCase);
    }

    @DisplayName("Teste de criação de cartão com sucesso")
    @Test
    void testCriarCartao_Successo() {
        // Arrange: Cria uma requisição de cartão e um mock de cartão esperado
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        Cartao cartaoMock = new Cartao("1234567890123456", "1234", BigDecimal.valueOf(500.00));
        when(criarCartaoUseCase.criarCartao(cartaoRequest)).thenReturn(cartaoMock);

        // Act: Chama o método de criação de cartão da controller
        ResponseEntity<CartaoResponse> response = cartaoController.criarCartao(cartaoRequest);

        // Assert: Verifica se a resposta tem status HTTP 201 (Created) e se o corpo da resposta contém o cartão correto
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getNumeroCartao()).isEqualTo(cartaoRequest.getNumeroCartao());
    }

    @DisplayName("Teste de criação de cartão com cartão existente")
    @Test
    void testCriarCartao_CartaoExistente() {
        // Arrange: Cria uma requisição de cartão com dados já existentes
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");

        // Simula o comportamento do caso de uso, lançando exceção para cartão existente
        when(criarCartaoUseCase.criarCartao(cartaoRequest))
                .thenThrow(new CartaoExistenteException(cartaoRequest.getSenha(), cartaoRequest.getNumeroCartao()));

        // Act & Assert: Verifica se a exceção CartaoExistenteException é lançada e a mensagem está correta
        CartaoExistenteException exception = assertThrows(CartaoExistenteException.class,
                () -> cartaoController.criarCartao(cartaoRequest));
        assertThat(exception.getMessage()).isEqualTo("O cartão [" + cartaoRequest.getNumeroCartao() + "] já existe.");
    }

    @DisplayName("Teste de consulta de saldo de cartão com sucesso")
    @Test
    void testObterSaldo_Successo() {
        // Arrange: Simula o comportamento do caso de uso, retornando um saldo para um cartão existente
        String numeroCartao = "1234567890123456";
        BigDecimal saldo = BigDecimal.valueOf(500.00);
        when(obterSaldoUseCase.obterSaldo(numeroCartao)).thenReturn(saldo);

        // Act: Chama o método de consulta de saldo da controller
        ResponseEntity<BigDecimal> response = cartaoController.obterSaldo(numeroCartao);

        // Assert: Verifica se a resposta tem status HTTP 200 (OK) e se o corpo da resposta contém o saldo correto
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualByComparingTo(saldo);
    }

    @DisplayName("Teste de consulta de saldo de cartão inexistente")
    @Test
    void testObterSaldo_CartaoInexistente() {
        // Arrange: Simula o comportamento do caso de uso, lançando exceção para cartão inexistente
        String numeroCartao = "1234567890123456";
        when(obterSaldoUseCase.obterSaldo(numeroCartao)).thenThrow(new CartaoInexistenteException(numeroCartao));

        // Act & Assert: Verifica se a exceção CartaoInexistenteException é lançada e a mensagem está correta
        CartaoInexistenteException exception = assertThrows(CartaoInexistenteException.class, () -> cartaoController.obterSaldo(numeroCartao));
        assertThat(exception.getMessage()).isEqualTo("O cartão [" + numeroCartao + "] não foi encontrado.");
    }
}
