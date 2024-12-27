package br.com.miniautorizador.unitarios.application.cartao;

import br.com.miniautorizador.application.cartao.CriarCartaoUseCaseImpl;
import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.service.CartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Representa um cartão de crédito ou débito.
 * <p>
 * Esta classe encapsula as informações de um cartão, incluindo o número do cartão, senha e saldo.
 * Ela fornece métodos para manipular essas informações e realizar operações relacionadas ao cartão.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class CriarCartaoUseCaseImplTest {

    @Mock
    private CartaoService cartaoService;

    @InjectMocks
    private CriarCartaoUseCaseImpl criarCartaoUseCaseImpl;

    private CartaoRequest cartaoRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartaoRequest = new CartaoRequest("1234567890123456", "senha123");
        criarCartaoUseCaseImpl = new CriarCartaoUseCaseImpl(cartaoService);
    }

    @DisplayName("Teste de criação de cartão com sucesso")
    @Test
    void testCriarCartao_Sucesso() {
        Cartao cartaoEsperado = new Cartao(cartaoRequest.getNumeroCartao(), "senhaHash", BigDecimal.valueOf(500.00));
        when(cartaoService.criarCartao(cartaoRequest)).thenReturn(cartaoEsperado);

        Cartao cartaoResultado = criarCartaoUseCaseImpl.criarCartao(cartaoRequest);

        verify(cartaoService, times(1)).criarCartao(cartaoRequest);
        assertNotNull(cartaoResultado);
        assertEquals(cartaoEsperado.getNumeroCartao(), cartaoResultado.getNumeroCartao());
        assertEquals(cartaoEsperado.getSaldo(), cartaoResultado.getSaldo());
    }

    @DisplayName("Teste de criação de cartão com cartão existente")
    @Test
    void testCriarCartao_ExceptionCartaoExistente() {
        when(cartaoService.criarCartao(cartaoRequest)).thenThrow(new CartaoExistenteException(cartaoRequest.getSenha(), cartaoRequest.getNumeroCartao()));

        CartaoExistenteException exception = assertThrows(CartaoExistenteException.class, () ->
                criarCartaoUseCaseImpl.criarCartao(cartaoRequest));

        assertEquals(cartaoRequest.getSenha(), exception.getSenha());
        assertEquals(cartaoRequest.getNumeroCartao(), exception.getNumeroCartao());
    }
}
