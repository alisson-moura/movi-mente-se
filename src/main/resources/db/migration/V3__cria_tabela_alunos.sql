CREATE TABLE usuarios
(
    id            UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    nome          VARCHAR(150)        NOT NULL,
    sobrenome     VARCHAR(150)        NOT NULL,
    email         VARCHAR(150) UNIQUE NOT NULL,
    senha         VARCHAR(255)        NOT NULL,
    criado_em     TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE alunos
(
    id              UUID PRIMARY KEY             DEFAULT gen_random_uuid(),
    nome            VARCHAR(150)        NOT NULL,
    sobrenome       VARCHAR(150)        NOT NULL,
    email           VARCHAR(150) UNIQUE NOT NULL,
    senha           VARCHAR(255)        NOT NULL,
    cpf             VARCHAR(11) UNIQUE  NOT NULL,
    cracha          VARCHAR(50) UNIQUE,
    telefone        VARCHAR(20),
    genero          CHAR(1)             NOT NULL,
    data_nascimento DATE                NOT NULL,
    ativo           BOOLEAN             NOT NULL DEFAULT TRUE,
    empresa_id      INTEGER             NOT NULL REFERENCES empresas (id),
    grupo_id        INTEGER             NOT NULL REFERENCES grupos (id),
    criado_em       TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP,
    atualizado_em   TIMESTAMP           NOT NULL DEFAULT CURRENT_TIMESTAMP
);