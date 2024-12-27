package br.com.miniautorizador.unitarios.application.cartao;

import br.com.miniautorizador.application.cartao.ObterSaldoUseCaseImpl;
import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ObterSaldoUseCaseImplTest {
    @Mock
    private CartaoRepository cartaoRepository;

    @InjectMocks
    private ObterSaldoUseCaseImpl obterSaldoUseCaseImpl;

    private Cartao cartao;
    private String numeroCartaoValido = "1234567890123456";
    private String numeroCartaoInvalido = "9999999999999999";

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        obterSaldoUseCaseImpl = new ObterSaldoUseCaseImpl(cartaoRepository);

        cartao = new Cartao(numeroCartaoValido, "senhaHash", BigDecimal.valueOf(500.00));
    }

    @DisplayName("Teste de obtenção de saldo com sucesso")
    @Test
    void testObterSaldo_Sucesso() {
        when(cartaoRepository.findByNumeroCartao(numeroCartaoValido)).thenReturn(Optional.of(cartao));
        BigDecimal saldo = obterSaldoUseCaseImpl.obterSaldo(numeroCartaoValido);

        assertNotNull(saldo);
        assertEquals(BigDecimal.valueOf(500.00), saldo);

        verify(cartaoRepository, times(1)).findByNumeroCartao(numeroCartaoValido);
    }

    @DisplayName("Teste de obtenção de saldo com cartão inexistente")
    @Test
    void testObterSaldo_CartaoInexistente() {
        when(cartaoRepository.findByNumeroCartao(numeroCartaoInvalido)).thenReturn(Optional.empty());

        CartaoInexistenteException exception = assertThrows(CartaoInexistenteException.class, () -> {
            obterSaldoUseCaseImpl.obterSaldo(numeroCartaoInvalido);
        });

        assertEquals(numeroCartaoInvalido, exception.getNumeroCartao());
        verify(cartaoRepository, times(1)).findByNumeroCartao(numeroCartaoInvalido);
    }
}
