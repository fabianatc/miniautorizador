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

    @Test
    void testCriarCartao_Successo() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        Cartao cartaoMock = new Cartao("1234567890123456", "1234", BigDecimal.valueOf(500.00));
        when(criarCartaoUseCase.criarCartao(cartaoRequest)).thenReturn(cartaoMock);

        ResponseEntity<CartaoResponse> response = cartaoController.criarCartao(cartaoRequest);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(Objects.requireNonNull(response.getBody()).getNumeroCartao()).isEqualTo(cartaoRequest.getNumeroCartao());
    }

    @Test
    void testCriarCartao_CartaoExistente() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        when(criarCartaoUseCase.criarCartao(cartaoRequest)).thenThrow(new CartaoExistenteException(cartaoRequest.getSenha(), cartaoRequest.getNumeroCartao()));

        CartaoExistenteException exception = assertThrows(CartaoExistenteException.class, () -> cartaoController.criarCartao(cartaoRequest));
        assertThat(exception.getMessage()).isEqualTo("O cartão [" + cartaoRequest.getNumeroCartao() + "] já existe.");
    }

    @Test
    void testObterSaldo_Successo() {
        String numeroCartao = "1234567890123456";
        BigDecimal saldo = BigDecimal.valueOf(500.00);
        when(obterSaldoUseCase.obterSaldo(numeroCartao)).thenReturn(saldo);

        ResponseEntity<BigDecimal> response = cartaoController.obterSaldo(numeroCartao);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualByComparingTo(saldo);
    }

    @Test
    void testObterSaldo_CartaoInexistente() {
        String numeroCartao = "1234567890123456";
        when(obterSaldoUseCase.obterSaldo(numeroCartao)).thenThrow(new CartaoInexistenteException(numeroCartao));

        CartaoInexistenteException exception = assertThrows(CartaoInexistenteException.class, () -> cartaoController.obterSaldo(numeroCartao));
        assertThat(exception.getMessage()).isEqualTo("O cartão [" + numeroCartao + "] não foi encontrado.");
    }
}
