INSERT INTO empresas (nome, cnpj)
VALUES ('Hospital de base', NULL),
       ('Hospital da criança e maternidade', NULL),
       ('Lucy Montoro', NULL),
       ('FAMERP', NULL);

INSERT INTO grupos (nome, descricao, pagante)
VALUES ('Colaborador', 'Funcionário CLT da empresa', TRUE),
       ('Estagiário', 'Estagiário vinculado à instituição', FALSE),
       ('Menor Aprendiz', 'Jovem aprendiz conforme Lei 10.097/2000', FALSE),
       ('Aluno', 'Aluno de graduação ou pós-graduação', FALSE),
       ('Médico Residente', 'Médico em programa de residência', FALSE);