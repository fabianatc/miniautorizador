package br.com.miniautorizador.unitarios.presentation.dto;

import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Classe de teste para a request de transação.
 * <p>
 * Essa classe é responsável por testar a validação e a estrutura da request de transação, garantindo que os dados sejam consistentes e válidos.
 * Os testes verificam se a request está sendo processada corretamente e se está retornando as mensagens de erro esperadas em caso de falha.
 *
 * @author Fabiana Costa
 */
@ExtendWith(MockitoExtension.class)
class TransacaoRequestTest {
    private static Validator validator;

    @BeforeAll
    static void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
    }

    @DisplayName("Teste de requisição de transação válida")
    @Test
    void testTransacaoRequest_Valido() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertTrue(violations.isEmpty(), "Não deve haver violações de validação para uma requisição válida.");
    }

    @DisplayName("Teste de validação de requisição de transação com número nulo")
    @Test
    void testNumeroCartao_Nulo() {
        TransacaoRequest request = new TransacaoRequest(null, "1234", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Deve haver uma violação para número do cartão nulo.");
        assertEquals("O número do cartão é obrigatório.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de transação com número de tamanho inválido")
    @Test
    void testNumeroCartao_TamanhoInvalido() {
        TransacaoRequest request = new TransacaoRequest("123456789012345", "1234", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size(), "Deve haver duas violações para número do cartão com tamanho inválido e caracteres inválidos.");

        boolean tamanhoInvalidoEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O número do cartão deve ter 16 dígitos."));
        boolean caracteresInvalidosEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("O número do cartão deve conter apenas dígitos."));

        assertTrue(tamanhoInvalidoEncontrado, "A mensagem 'O número do cartão deve ter 16 dígitos.' deve estar presente.");
        assertTrue(caracteresInvalidosEncontrado, "A mensagem 'O número do cartão deve conter apenas dígitos.' deve estar presente.");
    }

    @DisplayName("Teste de validação de requisição de transação com número de caracteres inválidos")
    @Test
    void testNumeroCartao_CaracteresInvalidos() {
        TransacaoRequest request = new TransacaoRequest("123456789012345A", "1234", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Deve haver uma violação para número do cartão com caracteres inválidos.");
        assertEquals("O número do cartão deve conter apenas dígitos.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de transação com senha nula")
    @Test
    void testSenhaCartao_Nula() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", null, BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(2, violations.size(), "Deve haver uma violação para senha do cartão nula.");
        assertEquals("A senha do cartão é obrigatória.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de transação com senha de tamanho inválido")
    @Test
    void testSenhaCartao_TamanhoInvalido() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "127", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);

        assertEquals(2, violations.size(), "Deve haver duas violações para senha do cartão com tamanho inválido e caracteres inválidos.");

        boolean tamanhoInvalidoEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("A senha do cartão deve ter 4 dígitos."));
        boolean caracteresInvalidosEncontrado = violations.stream()
                .anyMatch(v -> v.getMessage().equals("A senha do cartão deve conter apenas dígitos."));

        assertTrue(tamanhoInvalidoEncontrado, "A mensagem 'A senha do cartão deve ter 4 dígitos.' deve estar presente.");
        assertTrue(caracteresInvalidosEncontrado, "A mensagem 'A senha do cartão deve conter apenas dígitos.' deve estar presente.");
    }

    @DisplayName("Teste de validação de requisição de transação com senha de caracteres inválidos")
    @Test
    void testSenhaCartao_CaracteresInvalidos() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "12A4", BigDecimal.valueOf(100.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Deve haver uma violação para senha do cartão com caracteres inválidos.");
        assertEquals("A senha do cartão deve conter apenas dígitos.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de transação com valor nulo")
    @Test
    void testValor_Nulo() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", null);
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Deve haver uma violação para valor nulo.");
        assertEquals("O valor da transação é obrigatório.", violations.iterator().next().getMessage());
    }

    @DisplayName("Teste de validação de requisição de transação com valor inválido")
    @Test
    void testValor_Invalido() {
        TransacaoRequest request = new TransacaoRequest("1234567890123456", "1234", BigDecimal.valueOf(0.00));
        Set<ConstraintViolation<TransacaoRequest>> violations = validator.validate(request);
        assertEquals(1, violations.size(), "Deve haver uma violação para valor inválido.");
        assertEquals("O valor da transação deve ser maior que 0.", violations.iterator().next().getMessage());
    }
}
