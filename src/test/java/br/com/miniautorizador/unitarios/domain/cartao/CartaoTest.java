package br.com.miniautorizador.unitarios.domain.cartao;

import br.com.miniautorizador.domain.cartao.Cartao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Representa um cartão.
 * <p>
 * Esta classe encapsula as informações de um cartão, incluindo o número do cartão, senha e saldo.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class CartaoTest {

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        // Cria um cartão para ser usado nos testes
        cartao = new Cartao("1234567890123456", "1234", BigDecimal.valueOf(500.00));
    }

    @DisplayName("Teste para construir um cartão")
    @Test
    void testConstrutor() {
        // Verifica se o construtor inicializa os campos corretamente
        assertThat(cartao.getNumeroCartao()).isEqualTo("1234567890123456");
        assertThat(cartao.getSenha()).isEqualTo("1234");
        assertThat(cartao.getSaldo()).isEqualTo(BigDecimal.valueOf(500.00));
    }

    @DisplayName("Teste para setters e getters")
    @Test
    void testSettersEGetters() {
        // Testa os setters e getters
        cartao.setNumeroCartao("9876543210987654");
        cartao.setSenha("4321");
        cartao.setSaldo(BigDecimal.valueOf(1000.00));

        assertThat(cartao.getNumeroCartao()).isEqualTo("9876543210987654");
        assertThat(cartao.getSenha()).isEqualTo("4321");
        assertThat(cartao.getSaldo()).isEqualTo(BigDecimal.valueOf(1000.00));
    }

    @DisplayName("Teste para versão")
    @Test
    void testVersion() {
        // Verifica se a versão é inicializada como nula
        assertThat(cartao.getVersion()).isNull();

        // Define uma versão e verifica
        cartao.setVersion(1L);
        assertThat(cartao.getVersion()).isEqualTo(1L);
    }
}
