CREATE TABLE empresas
(
    id            SERIAL PRIMARY KEY,
    nome          VARCHAR(150) NOT NULL,
    cnpj          VARCHAR(14),
    ativo         BOOLEAN      NOT NULL DEFAULT TRUE,
    criada_em     TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizada_em TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);