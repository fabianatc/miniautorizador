package br.com.miniautorizador.unitarios.infrastructure.repository;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de teste para CartaoRepository.
 * <p>
 * Esta classe contém casos de teste para verificar a funcionalidade da classe CartaoRepository,
 * garantindo acesso e manipulação de dados corretos para operações relacionadas a cartões no banco de dados.
 *
 * @author Fabiana Costa
 */
@DataJpaTest
class CartaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager; // Gerenciador de entidades para testes

    @Autowired
    private CartaoRepository cartaoRepository; // Repositório a ser testado

    private Cartao cartao;

    @BeforeEach
    void setUp() {
        // Cria um cartão para ser usado nos testes
        cartao = new Cartao("1234567890123456", "1234", BigDecimal.valueOf(500.00));
        entityManager.persistAndFlush(cartao);
    }

    @DisplayName("Teste de busca de cartão pelo número - Cartão existente")
    @Test
    void testFindByNumeroCartao_CartaoExiste() {
        // Busca o cartão pelo número
        Optional<Cartao> encontrado = cartaoRepository.findByNumeroCartao("1234567890123456");

        // Verifica se o cartão foi encontrado
        assertThat(encontrado).isPresent();
        assertThat(encontrado.get().getNumeroCartao()).isEqualTo("1234567890123456");
        assertThat(encontrado.get().getSenha()).isEqualTo("1234");
        assertThat(encontrado.get().getSaldo()).isEqualTo(BigDecimal.valueOf(500.00));
    }

    @DisplayName("Teste de busca de cartão pelo número - Cartão inexistente")
    @Test
    void testFindByNumeroCartao_CartaoNaoExiste() {
        Optional<Cartao> encontrado = cartaoRepository.findByNumeroCartao("0000000000000000");

        // Verifica que o cartão não foi encontrado
        assertThat(encontrado).isNotPresent();
    }

    @DisplayName("Teste de verificação de existência de cartão pelo número")
    @Test
    void testExistsByNumeroCartao_CartaoExiste() {
        // Verifica se o cartão existe pelo número
        boolean existe = cartaoRepository.existsByNumeroCartao("1234567890123456");

        // Verifica que o cartão existe
        assertThat(existe).isTrue();
    }

    @DisplayName("Teste de verificação de existência de cartão pelo número - Cartão inexistente")
    @Test
    void testExistsByNumeroCartao_CartaoNaoExiste() {
        // Verifica se um cartão não existe
        boolean existe = cartaoRepository.existsByNumeroCartao("0000000000000000");

        // Verifica que o cartão não existe
        assertThat(existe).isFalse();
    }

    @DisplayName("Teste para salvar um cartão")
    @Test
    void testSaveCartao() {
        // Cria um novo cartão
        Cartao novoCartao = new Cartao("9876543210987654", "4321", BigDecimal.valueOf(1000.00));

        // Salva o cartão no repositório
        Cartao salvo = cartaoRepository.save(novoCartao);

        // Verifica se o cartão foi salvo corretamente
        assertThat(salvo).isNotNull();
        assertThat(salvo.getNumeroCartao()).isEqualTo("9876543210987654");
        assertThat(salvo.getSenha()).isEqualTo("4321");
        assertThat(salvo.getSaldo()).isEqualTo(BigDecimal.valueOf(1000.00));
    }

    @DisplayName("Teste para remover um cartão")
    @Test
    void testDeleteCartao() {
        // Remove o cartão do banco de dados
        cartaoRepository.delete(cartao);

        // Verifica se o cartão foi removido
        Optional<Cartao> encontrado = cartaoRepository.findByNumeroCartao("1234567890123456");
        assertThat(encontrado).isNotPresent();
    }
}
