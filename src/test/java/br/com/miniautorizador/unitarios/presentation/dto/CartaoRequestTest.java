package br.com.miniautorizador.unitarios.presentation.dto;

import br.com.miniautorizador.presentation.dto.CartaoRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


/**
 * Classe de teste para a requisição de cartão.
 * <p>
 * Essa classe é responsável por testar a validação e a estrutura da requisição de cartão, garantindo que os dados sejam consistentes e válidos.
 * Os testes verificam se a requisição está sendo processada corretamente e se está retornando as mensagens de erro esperadas em caso de falha.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class CartaoRequestTest {

    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Teste de validação de requisição de cartão válido")
    @Test
    void testCartaoRequest_Valido() {
        CartaoRequest request = new CartaoRequest("1234567890123456", "1234");
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação para uma requisição válida.");
    }

    @DisplayName("Teste de validação de requisição de cartão com número nulo")
    @Test
    void testNumeroCartao_Nulo() {
        // Arrange: Cria uma requisição de cartão com número nulo
        CartaoRequest request = new CartaoRequest(null, "1234");
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para número do cartão nulo foi encontrada
        assertEquals(2, violations.size(), "Deve haver uma violação para número do cartão nulo.");
        assertEquals("O número do cartão é obrigatório.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de cartão com tamanho do número inválido")
    @Test
    void testNumeroCartao_TamanhoInvalido() {
        // Arrange: Cria uma requisição de cartão com tamanho do número inválido
        CartaoRequest request = new CartaoRequest("123456789012345", "1234");
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para tamanho do número do
        // cartão inválido foi encontrada
        assertEquals(2, violations.size(), "Deve haver duas violações para número do cartão com tamanho inválido e caracteres inválidos.");

        boolean tamanhoInvalidoEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O número do cartão deve ter 16 dígitos."));
        boolean caracteresInvalidosEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O número do cartão deve conter apenas dígitos."));

        assertTrue(tamanhoInvalidoEncontrado, "A mensagem 'O número do cartão deve ter 16 dígitos.' deve estar presente.");
        assertTrue(caracteresInvalidosEncontrado, "A mensagem 'O número do cartão deve conter apenas dígitos.' deve estar presente.");
    }

    @DisplayName("Teste de validação de requisição de cartão com caracteres do número inválidos")
    @Test
    void testNumeroCartao_CaracteresInvalidos() {
        // Arrange: Cria uma requisição de cartão com caracteres do número inválidos
        CartaoRequest request = new CartaoRequest("123456789012345A", "1234");

        // Act: Valida a requisição
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para caracteres do número do
        // cartão inválidos foi encontrada
        assertEquals(1, violations.size(), "Deve haver uma violação para número do cartão com caracteres inválidos.");
        assertEquals("O número do cartão deve conter apenas dígitos.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de cartão com senha nula")
    @Test
    void testSenha_Nula() {
        // Arrange: Cria uma requisição de cartão com senha nula
        CartaoRequest request = new CartaoRequest("1234567890123456", null);

        // Act: Valida a requisição
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para senha nula foi encontrada
        assertEquals(2, violations.size(), "Deve haver uma violação para senha nula.");
        assertEquals("A senha do cartão é obrigatória.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de cartão com senha de tamanho inválido")
    @Test
    void testSenha_TamanhoInvalido() {
        // Arrange: Cria uma requisição de cartão com senha de tamanho inválido
        CartaoRequest request = new CartaoRequest("1234567890123456", "123");

        // Act: Valida a requisição
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para tamanho da senha inválido
        // foi encontrada
        assertEquals(2, violations.size(), "Deve haver duas violações para senha do cartão com tamanho inválido e caracteres inválidos.");

        boolean tamanhoInvalidoEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("A senha do cartão deve ter 4 dígitos."));
        boolean caracteresInvalidosEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("A senha do cartão deve conter apenas dígitos."));

        assertTrue(tamanhoInvalidoEncontrado, "A mensagem 'A senha do cartão deve ter 4 dígitos.' deve estar presente.");
        assertTrue(caracteresInvalidosEncontrado, "A mensagem 'A senha do cartão deve conter apenas dígitos.' deve estar presente.");
    }

    @DisplayName("Teste de validação de requisição de cartão com senha com caracteres inválidos")
    @Test
    void testSenha_CaracteresInvalidos() {
        // Arrange: Cria uma requisição de cartão com senha contendo caracteres inválidos
        CartaoRequest request = new CartaoRequest("1234567890123456", "12A4");

        // Act: Valida a requisição
        Set<ConstraintViolation<CartaoRequest>> violations = validator.validate(request);

        // Assert: Verifica se a violação de validação para caracteres inválidos na senha foi encontrada
        assertEquals(1, violations.size(), "Deve haver uma violação para senha com caracteres inválidos.");
        assertEquals("A senha do cartão deve conter apenas dígitos.", violations.iterator().next().getMessage());
    }
}
