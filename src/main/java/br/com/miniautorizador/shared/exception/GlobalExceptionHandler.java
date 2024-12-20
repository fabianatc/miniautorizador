package br.com.miniautorizador.shared.exception;

import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.domain.cartao.exception.CartaoInexistenteException;
import br.com.miniautorizador.presentation.dto.CartaoResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
}
