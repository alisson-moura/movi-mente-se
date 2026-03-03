package com.funfarme.movimente_se.aluno;

import com.funfarme.movimente_se.BaseE2ETest;
import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class CadastrarAlunoE2ETest extends BaseE2ETest {
    private AlunoTestDataBuilder testDataBuilder;

    @BeforeEach
    public void configurarRequest() {
        testDataBuilder = new AlunoTestDataBuilder(this.jdbcTemplate, this.clock);
    }

    @Test
    @DisplayName("Deve cadastrar um aluno com sucesso.")
    public void cadastro_com_dados_validos() {
        RequestCadastrarAlunoDto request = testDataBuilder.build();

        client.post().uri("/alunos")
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        Integer quantidadeDeAlunosNoDb = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE email = ?",
                Integer.class,
                request.email()
        );

        Assertions.assertEquals(1, quantidadeDeAlunosNoDb, "Deveria existir exatamente 1 aluno com este email no banco de dados");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com e-mail duplicado.")
    public void cadastro_com_email_em_uso() {
        String emailDuplicado = "email_duplicado@mail.com";

        RequestCadastrarAlunoDto alunoJaCadastrado = testDataBuilder
                .comEmail(emailDuplicado)
                .build();
        testDataBuilder.salvarNoBanco(alunoJaCadastrado);

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comEmail(emailDuplicado)
                .comCpf()
                .comCracha()
                .build();

        client.post().uri("/alunos")
                .body(novoAlunoRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Este e-mail já está em uso por outro aluno.")
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Duplicated Resource");

        Integer quantidadeDeAlunosNoDb = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE email = ?",
                Integer.class,
                emailDuplicado
        );

        Assertions.assertEquals(1, quantidadeDeAlunosNoDb, "Deveria existir somente 1 aluno com este email no banco de dados");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com cpf duplicado.")
    public void cadastro_com_cpf_em_uso() {
        String cpfDuplicado = "42692470885";

        RequestCadastrarAlunoDto alunoJaCadastrado = testDataBuilder
                .comCpf(cpfDuplicado)
                .build();
        testDataBuilder.salvarNoBanco(alunoJaCadastrado);

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comCpf(cpfDuplicado)
                .comCracha()
                .comEmail()
                .build();

        client.post().uri("/alunos")
                .body(novoAlunoRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Este CPF já está em uso por outro aluno.")
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Duplicated Resource");

        Integer quantidadeDeAlunosNoDb = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE cpf = ?",
                Integer.class,
                cpfDuplicado
        );

        Assertions.assertEquals(1, quantidadeDeAlunosNoDb, "Deveria existir somente 1 aluno com este CPF no banco de dados");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com crachá duplicado.")
    public void cadastro_com_cracha_em_uso() {
        String crachaDuplicado = "15724";

        RequestCadastrarAlunoDto alunoJaCadastrado = testDataBuilder
                .comCracha(crachaDuplicado)
                .build();
        testDataBuilder.salvarNoBanco(alunoJaCadastrado);

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comCracha(crachaDuplicado)
                .comCpf()
                .comEmail()
                .build();

        client.post().uri("/alunos")
                .body(novoAlunoRequest)
                .exchange()
                .expectStatus().is4xxClientError()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Este crachá já está em uso por outro aluno.")
                .jsonPath("$.status").isEqualTo(409)
                .jsonPath("$.error").isEqualTo("Duplicated Resource");

        Integer quantidadeDeAlunosNoDb = this.jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE cracha = ?",
                Integer.class,
                crachaDuplicado
        );

        Assertions.assertEquals(1, quantidadeDeAlunosNoDb, "Deveria existir somente 1 aluno com este crachá no banco de dados");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno informando um id de empresa não existente")
    public void cadastro_com_empresa_id_nao_existente(){
        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comEmpresaIdInvalido()
                .build();

        client.post().uri("/alunos")
                .body(novoAlunoRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Não foi encontrado uma empresa com o id informado.")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found Resource");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno informando um id de grupo não existente")
    public void cadastro_com_grupo_id_nao_existente(){
        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comGrupoIdInvalido()
                .build();

        client.post().uri("/alunos")
                .body(novoAlunoRequest)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.message").isEqualTo("Não foi encontrado um grupo com o id informado.")
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.error").isEqualTo("Not Found Resource");
    }
}
