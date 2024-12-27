package br.com.miniautorizador.unitarios.service;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.service.CartaoService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

/**
 * Classe de teste para o serviço de cartão.
 * <p>
 * Essa classe é responsável por testar a lógica de negócios do serviço de cartão, garantindo que as operações sejam executadas corretamente e de acordo com as regras de negócios.
 * Os testes verificam se o serviço está funcionando corretamente, incluindo a criação e consulta de saldo de cartões.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class CartaoServiceTest {
    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private CartaoService cartaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartaoService = new CartaoService(cartaoRepository, passwordEncoder);
    }

    @DisplayName("Teste de criação de cartão com sucesso")
    @Test
    void testCriarCartao_Successo() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        Cartao cartaoMock = new Cartao("1234567890123456", "encoded_password", BigDecimal.valueOf(500.00));
        when(passwordEncoder.encode("1234")).thenReturn("encoded_password");
        when(cartaoRepository.saveAndFlush(any(Cartao.class))).thenReturn(cartaoMock);
        Cartao cartao = cartaoService.criarCartao(cartaoRequest);

        assertThat(cartao).isNotNull();
        assertThat(cartao.getNumeroCartao()).isEqualTo(cartaoRequest.getNumeroCartao());
        assertThat(passwordEncoder.matches(cartaoRequest.getSenha(), cartao.getSenha())).isFalse(); // encoded password mismatch
        assertThat(cartao.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
    }

    @DisplayName("Teste de criação de cartão com cartão existente")
    @Test
    void testCriarCartao_CartaoExistente() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        when(cartaoRepository.saveAndFlush(any(Cartao.class))).thenThrow(DataIntegrityViolationException.class);

        assertThrows(CartaoExistenteException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }

    @DisplayName("Teste de criação de cartão com request null")
    @Test
    void testCriarCartao_CartaoRequestNull() {
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(null));
    }

    @DisplayName("Teste de criação de cartão com numero do cartao null")
    @Test
    void testCriarCartao_NumeroCartaoNull() {
        CartaoRequest cartaoRequest = new CartaoRequest(null, "1234");
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }

    @DisplayName("Teste de criação de cartão com senha null")
    @Test
    void testCriarCartao_SenhaNull() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", null);
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }
}
