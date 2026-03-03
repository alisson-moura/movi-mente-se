CREATE TABLE sessoes_alunos
(
    id        SERIAL PRIMARY KEY,
    token     CHAR(96) UNIQUE NOT NULL,
    aluno_id  INTEGER UNIQUE  NOT NULL REFERENCES alunos (id),
    expira_em TIMESTAMP       NOT NULL,
    criado_em TIMESTAMP       NOT NULL DEFAULT CURRENT_TIMESTAMP
);