CREATE TABLE cartao (
    numero_cartao VARCHAR(16) NOT NULL,
    senha VARCHAR(60) NOT NULL,
    saldo DECIMAL(10, 2) NOT NULL DEFAULT 500.00,
    version BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (numero_cartao)
);