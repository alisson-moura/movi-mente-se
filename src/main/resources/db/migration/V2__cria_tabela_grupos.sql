CREATE TABLE grupos
(
    id            SERIAL PRIMARY KEY,
    nome          VARCHAR(100) NOT NULL,
    descricao     VARCHAR(340),
    pagante       BOOLEAN      NOT NULL DEFAULT TRUE,
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criado_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);