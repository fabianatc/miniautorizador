package br.com.miniautorizador.integracao;

import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import br.com.miniautorizador.presentation.dto.TransacaoRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de teste de integração para o controlador de transações de cartões.
 * <p>
 * Essa classe é responsável por testar a integração do controlador de transações de débito com a aplicação.
 *
 * @author Fabiana Costa
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class TransacaoControllerIntegrationTest {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @LocalServerPort
    private int port;

    private HttpHeaders headers;

    @BeforeEach
    void setUp() {
        headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth("username", "password");
    }

    @DisplayName("Teste de transação de debito de cartão com sucesso")
    @Test
    void testExecutarTransacao_Sucesso() throws Exception {
        // Cadastrar um cartão
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123457", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);

        // Executar transação de débito
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890123457", "1234", BigDecimal.valueOf(100.00));
        HttpEntity<String> entity2 = new HttpEntity<>(objectMapper.writeValueAsString(transacaoRequest), headers);

        String url2 = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url2, HttpMethod.POST, entity2, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("OK");
    }

    @DisplayName("Teste de transação de debito de cartão com cartão inexistente")
    @Test
    void testExecutarTransacao_CartaoInexistente() throws Exception {
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890963258", "1234", BigDecimal.valueOf(100.00));
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(transacaoRequest), headers);

        String url = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("CARTAO_INEXISTENTE");
    }

    @DisplayName("Teste de transação de debito de cartão com senha inválida")
    @Test
    void testExecutarTransacao_SenhaInvalida() throws Exception {
        // Cadastrar um cartão
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890198745", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);

        // Executar transação de débito
        TransacaoRequest transacaoRequest = new TransacaoRequest("1234567890198745", "4321", BigDecimal.valueOf(100.00));
        HttpEntity<String> entity2 = new HttpEntity<>(objectMapper.writeValueAsString(transacaoRequest), headers);

        String url2 = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url2, HttpMethod.POST, entity2, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("SENHA_INVALIDA");
    }

    @DisplayName("Teste de transação de debito de cartão com saldo insuficiente")
    @Test
    void testExecutarTransacao_SaldoInsuficiente() throws Exception {
        // Cadastrar um cartão
        CartaoRequest cartaoRequest = new CartaoRequest("7418529632587410", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);

        // Executar transação de débito
        TransacaoRequest transacaoRequest = new TransacaoRequest("7418529632587410", "1234", BigDecimal.valueOf(600.00));
        HttpEntity<String> entity2 = new HttpEntity<>(objectMapper.writeValueAsString(transacaoRequest), headers);

        String url2 = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url2, HttpMethod.POST, entity2, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(422);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("SALDO_INSUFICIENTE");
    }

    @DisplayName("Teste de transação de debito de cartão com request nulo")
    @Test
    void testExecutarTransacao_RequestNula() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(null), headers);

        String url = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody()).isEqualTo("REQUEST_INVALIDA");
    }

    @DisplayName("Teste de transação de debito de cartão sem autenticação")
    @Test
    void testExecutarTransacao_SemAutenticacao() throws Exception {
        TransacaoRequest transacaoRequest = new TransacaoRequest("7418529632587410", "1234", BigDecimal.valueOf(100.00));

        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(transacaoRequest), headers1);

        String url = "http://localhost:" + port + "/transacoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}