package br.com.miniautorizador.shared.exception;

import br.com.miniautorizador.domain.cartao.exception.*;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(CartaoExistenteException.class)
    public ResponseEntity<CartaoResponse> handleCartaoExistenteException(CartaoExistenteException e) {
        logger.error("Erro ao criar cartão: {}", e.getMessage());
        CartaoResponse response = new CartaoResponse(e.getNumeroCartao(), e.getSenha());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @ExceptionHandler(CartaoInexistenteException.class)
    public ResponseEntity<Void> handleCartaoInexistenteException(CartaoInexistenteException e) {
        logger.error("Cartão inexistente: {}", e.getNumeroCartao());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @ExceptionHandler(CartaoInexistenteTransacaoException.class)
    public ResponseEntity<String> handleCartaoInexistenteTransacaoException(CartaoInexistenteTransacaoException e) {
        logger.error("Cartão inexistente: {}", e.getNumeroCartao());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("CARTAO_INEXISTENTE");
    }

    // Tratamento para SenhaInvalidaException
    @ExceptionHandler(SenhaInvalidaException.class)
    public ResponseEntity<String> handleSenhaInvalidaException(SenhaInvalidaException e) {
        logger.error("Senha inválida: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("SENHA_INVALIDA");
    }

    // Tratamento para SaldoInsuficienteException
    @ExceptionHandler(SaldoInsuficienteException.class)
    public ResponseEntity<String> handleSaldoInsuficienteException(SaldoInsuficienteException e) {
        logger.error("Saldo insuficiente: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("SALDO_INSUFICIENTE");
    }

    // Tratamento para OptimisticLockingFailureException
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<String> handleOptimisticLockingFailure(OptimisticLockingFailureException e) {
        logger.error("Conflito de concorrência: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body("CONFLITO_DE_CONCORRENCIA");
    }

    // Tratamento para DataIntegrityViolationException
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<String> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        logger.error("Violação de integridade de dados: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body("DADOS_INVALIDOS");
    }

    // Tratamento para MethodArgumentNotValidException
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleGenericException(MethodArgumentNotValidException e) {
        logger.error("Validação de dados inválida: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("DADOS_INVALIDOS");
    }

    // Tratamento para outras exceções genéricas
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGenericException(Exception e) {
        logger.error("Erro interno do servidor: {}", e.getMessage(), e);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("ERRO_INTERNO");
    }
}
