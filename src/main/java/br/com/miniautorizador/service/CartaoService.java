package br.com.miniautorizador.service;

import br.com.miniautorizador.domain.cartao.Cartao;
import br.com.miniautorizador.domain.cartao.exception.CartaoExistenteException;
import br.com.miniautorizador.infrastructure.repository.CartaoRepository;
import br.com.miniautorizador.presentation.dto.CartaoRequest;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class CartaoService {
    private final CartaoRepository cartaoRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public CartaoService(CartaoRepository cartaoRepository, PasswordEncoder passwordEncoder) {
        this.cartaoRepository = cartaoRepository;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Cria um novo cartão com saldo inicial de R$500,00.
     *
     * @param cartaoRequest Dados do cartão a ser criado.
     * @return Cartão criado.
     */
    @Transactional
    public Cartao criarCartao(CartaoRequest cartaoRequest) {
        try {
            String senhaHash = passwordEncoder.encode(cartaoRequest.getSenha());

            Cartao cartao = new Cartao(
                    cartaoRequest.getNumeroCartao(),
                    senhaHash,
                    BigDecimal.valueOf(500.00)
            );

            return cartaoRepository.saveAndFlush(cartao);
        } catch (DataIntegrityViolationException e) {
            throw new CartaoExistenteException(cartaoRequest.getSenha(), cartaoRequest.getNumeroCartao());
        }
    }
}