package br.com.miniautorizador.unitarios.presentation.dto;

import br.com.miniautorizador.presentation.dto.CartaoResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Classe de teste para a response de cartão.
 * <p>
 * Essa classe é responsável por testar a estrutura e o conteúdo da response de cartão, garantindo que os dados sejam retornados corretamente e de acordo com as expectativas.
 * Os testes verificam se a resposta está sendo gerada corretamente e se está contendo as informações esperadas.
 *
 * @author Fabiana Costa
 */
class CartaoResponseTest {
    @DisplayName("Teste do construtor e dos getters")
    @Test
    void testConstrutorEGetters() {
        CartaoResponse response = new CartaoResponse("1234567890123456", "1234");

        assertEquals("1234567890123456", response.getNumeroCartao(), "O número do cartão deve ser '1234567890123456'.");
        assertEquals("1234", response.getSenha(), "A senha deve ser '1234'.");
    }

    @DisplayName("Teste dos setters")
    @Test
    void testSetters() {
        CartaoResponse response = new CartaoResponse("0000000000000000", "0000");

        response.setNumeroCartao("9876543210987654");
        response.setSenha("4321");

        assertEquals("9876543210987654", response.getNumeroCartao(), "O número do cartão deve ser '9876543210987654'.");
        assertEquals("4321", response.getSenha(), "A senha deve ser '4321'.");
    }

    @DisplayName("Teste de equals e hashCode")
    @Test
    void testEqualsAndHashCode() {
        CartaoResponse response1 = new CartaoResponse("1234567890123456", "1234");
        CartaoResponse response2 = new CartaoResponse("1234567890123456", "1234");

        assertEquals(response1, response2, "Os objetos CartaoResponse devem ser iguais.");
        assertEquals(response1.hashCode(), response2.hashCode(), "Os hashCodes devem ser iguais.");
    }

    @DisplayName("Teste do toString")
    @Test
    void testToString() {
        CartaoResponse response = new CartaoResponse("1234567890123456", "1234");

        String expectedToString = "CartaoResponse(numeroCartao=1234567890123456, senha=1234)";
        assertEquals(expectedToString, response.toString(), "O método toString deve retornar a representação correta.");
    }
}
