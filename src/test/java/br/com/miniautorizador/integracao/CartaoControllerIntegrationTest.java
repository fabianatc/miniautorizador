package br.com.miniautorizador.integracao;

import br.com.miniautorizador.presentation.dto.CartaoRequest;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
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

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Classe de teste de integração para a controller de cartões.
 * <p>
 * Essa classe é responsável por testar a integração da controller de cartões com a aplicação.
 *
 * @author Fabiana Costa
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@AutoConfigureMockMvc
class CartaoControllerIntegrationTest {
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

    @DisplayName("Teste de criação de cartão com sucesso")
    @Test
    void testCriarCartao_Sucesso() throws Exception {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        ResponseEntity<CartaoResponse> response = restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);

        assertThat(response.getStatusCode().value()).isEqualTo(201);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getNumeroCartao()).isEqualTo("1234567890123456");
        assertThat(response.getBody().getSenha()).isEqualTo("1234");
    }

    @DisplayName("Teste de criação de cartão com cartão existente")
    @Test
    void testCriarCartao_CartaoExistente() throws Exception {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123457", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);
        ResponseEntity<CartaoResponse> cartaoExistente = restTemplate.exchange(url, HttpMethod.POST, entity, CartaoResponse.class);

        assertThat(cartaoExistente.getStatusCode().value()).isEqualTo(422);
        assertThat(cartaoExistente.getBody()).isNotNull();
        assertThat(cartaoExistente.getBody().getNumeroCartao()).isEqualTo("1234567890123457");
        assertThat(cartaoExistente.getBody().getSenha()).isEqualTo("1234");
    }

    @DisplayName("Teste de criação de cartão com dados inválidos")
    @Test
    void testCriarCartao_DadosInvalidos() throws Exception {
        CartaoRequest cartaoRequest = new CartaoRequest("123456789012345", "12");

        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String url = "http://localhost:" + port + "/cartoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("DADOS_INVALIDOS");
    }

    @DisplayName("Teste de criação de cartão com dados nulos")
    @Test
    void testCriarCartao_DadosNulos() throws Exception {
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(null), headers);

        String url = "http://localhost:" + port + "/cartoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(400);
        assertThat(response.getBody()).isEqualTo("REQUEST_INVALIDA");
    }

    @DisplayName("Teste de criação de cartão sem autenticação")
    @Test
    void testCriarCartao_SemAutenticacao() throws Exception {
        CartaoRequest cartaoRequest = new CartaoRequest("1234567890123456", "1234");

        HttpHeaders headers1 = new HttpHeaders();
        headers1.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers1);

        String url = "http://localhost:" + port + "/cartoes";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }

    @DisplayName("Teste de obter saldo de cartão inexistente")
    @Test
    void testObterSaldo_CartaoInexistente() {
        String numeroCartao = "9876543210987654";

        String url = "http://localhost:" + port + "/cartoes/{numeroCartao}";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class, numeroCartao);

        assertThat(response.getStatusCode().value()).isEqualTo(404);
        assertThat(response.getBody()).isNull();
    }

    @DisplayName("Teste de obter saldo de cartão existente")
    @Test
    void testObterSaldo_CartaoExistente() throws Exception {
        CartaoRequest cartaoRequest = new CartaoRequest("9898989898989898", "1234");
        HttpEntity<String> entity = new HttpEntity<>(objectMapper.writeValueAsString(cartaoRequest), headers);

        String urlCadastro = "http://localhost:" + port + "/cartoes";
        restTemplate.exchange(urlCadastro, HttpMethod.POST, entity, CartaoResponse.class);

        String url = "http://localhost:" + port + "/cartoes/{numeroCartao}";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class, "9898989898989898");

        assertThat(response.getStatusCode().value()).isEqualTo(200);
        assertThat(response.getBody()).isEqualTo("500.00");
    }

    @DisplayName("Teste de obter saldo de cartão sem autenticação")
    @Test
    void testObterSaldo_SemAutenticacao() {
        String url = "http://localhost:" + port + "/cartoes/{numeroCartao}";
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, null, String.class, "9898989898989898");

        assertThat(response.getStatusCode().value()).isEqualTo(401);
    }
}