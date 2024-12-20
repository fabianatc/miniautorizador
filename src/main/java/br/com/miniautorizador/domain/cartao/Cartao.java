package br.com.miniautorizador.domain.cartao;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Entity
@Table(name = "cartoes", uniqueConstraints = @UniqueConstraint(columnNames = "numero_cartao"))
@Getter
@Setter
@NoArgsConstructor
public class Cartao {
    @Id
    @Column(name = "numero_cartao", length = 16, nullable = false, unique = true)
    private String numeroCartao;

    @Column(nullable = false)
    private String senha;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Version
    private Long version;

    public Cartao(String numeroCartao, String senha, BigDecimal saldo) {
        this.numeroCartao = numeroCartao;
        this.senha = senha;
        this.saldo = saldo;
    }
}

