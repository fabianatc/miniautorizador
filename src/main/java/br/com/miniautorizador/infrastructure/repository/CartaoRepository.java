package br.com.miniautorizador.infrastructure.repository;

import br.com.miniautorizador.domain.cartao.Cartao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CartaoRepository extends JpaRepository<Cartao, String> {
    Optional<Cartao> findByNumeroCartao(String numeroCartao);

    boolean existsByNumeroCartao(String numeroCartao);
}
