package br.com.miniautorizador.service;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CartaoServiceTest {
    @Mock
    private CartaoRepository cartaoRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Test
    void testCriarCartaoComSuccesso() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        Cartao cartaoMock = new Cartao("1234567890123456", "encoded_password", BigDecimal.valueOf(500.00));
        when(passwordEncoder.encode("1234")).thenReturn("encoded_password");
        when(cartaoRepository.saveAndFlush(any(Cartao.class))).thenReturn(cartaoMock);
        Cartao cartao = getCartaoService().criarCartao(cartaoRequest);

        assertThat(cartao).isNotNull();
        assertThat(cartao.getNumeroCartao()).isEqualTo(cartaoRequest.getNumeroCartao());
        assertThat(passwordEncoder.matches(cartaoRequest.getSenha(), cartao.getSenha())).isFalse(); // encoded password mismatch
        assertThat(cartao.getSaldo()).isEqualByComparingTo(BigDecimal.valueOf(500.00));
    }


    @Test
    void testCriarCartao_CartaoExistente() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        when(cartaoRepository.saveAndFlush(any(Cartao.class))).thenThrow(DataIntegrityViolationException.class);
        CartaoService cartaoService = getCartaoService();

        assertThrows(CartaoExistenteException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }

    @Test
    void testCriarCartao_CartaoRequestNull() {
        CartaoService cartaoService = getCartaoService();
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(null));
    }

    @Test
    void testCriarCartao_NumeroCartaoNull() {
        CartaoRequest cartaoRequest = new CartaoRequest(null, "1234");
        CartaoService cartaoService = getCartaoService();
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }

    @Test
    void testCriarCartao_SenhaNull() {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", null);
        CartaoService cartaoService = getCartaoService();
        assertThrows(NullPointerException.class, () -> cartaoService.criarCartao(cartaoRequest));
    }

    @Test
    void testObterSaldoComSuccesso() {
        String numeroCartao = "1234567890123456";
        BigDecimal saldo = BigDecimal.valueOf(500.00);
        Cartao cartaoMock = new Cartao(numeroCartao, "senha", saldo);
        when(cartaoRepository.findByNumeroCartao(numeroCartao)).thenReturn(Optional.of(cartaoMock));
        BigDecimal result = getCartaoService().obterSaldo(numeroCartao);

        assertThat(result).isEqualByComparingTo(saldo);
    }

    @Test
    void testObterSaldo_CartaoInexistente() {
        String numeroCartao = "1234567890123456";
        when(cartaoRepository.findByNumeroCartao(numeroCartao)).thenReturn(Optional.empty());
        CartaoService service = getCartaoService();
        
        assertThrows(CartaoInexistenteException.class, () -> service.obterSaldo(numeroCartao));
    }

    private CartaoService getCartaoService() {
        return new CartaoService(cartaoRepository, passwordEncoder);
    }
}
