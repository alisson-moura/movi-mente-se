CREATE TABLE sessoes
(
    id             SERIAL PRIMARY KEY,
    token          CHAR(96) UNIQUE NOT NULL,
    usuario_id     UUID            NOT NULL,
    data_expiracao TIMESTAMP       NOT NULL,
    iniciada_em    TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);