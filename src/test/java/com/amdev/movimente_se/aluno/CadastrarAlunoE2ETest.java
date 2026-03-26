package com.amdev.movimente_se.aluno;

import com.amdev.movimente_se.BaseE2ETest;
import com.amdev.movimente_se.dto.RequestCadastrarAlunoDto;
import com.amdev.movimente_se.entity.Aluno;
import com.amdev.movimente_se.repository.AlunoRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Optional;

class CadastrarAlunoE2ETest extends BaseE2ETest {

    private AlunoTestDataBuilder testDataBuilder;

    @Autowired
    private AlunoTestFactory alunoTestFactory;

    @Autowired
    private AlunoRepository alunoRepository;

    @BeforeEach
    public void configurarRequest() {
        testDataBuilder = new AlunoTestDataBuilder(this.jdbcTemplate);
    }

    @Test
    @DisplayName("Deve cadastrar um aluno com sucesso.")
    public void cadastro_com_dados_validos() {
        RequestCadastrarAlunoDto request = testDataBuilder
                .comGrupoValido()
                .comEmpresaValida()
                .build();

        client.post().uri("/alunos")
                .body(request)
                .exchange()
                .expectStatus().isCreated();

        Optional<Aluno> alunoSalvo = alunoRepository.procurarPorEmail(request.email());
        Assertions.assertTrue(alunoSalvo.isPresent(), "Deveria existir o aluno no banco de dados");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com e-mail duplicado.")
    public void cadastro_com_email_em_uso() {
        String emailDuplicado = "email_duplicado@mail.com";
        alunoTestFactory.salvarNovoAluno(
                testDataBuilder
                        .comEmail(emailDuplicado)
                        .comGrupoValido()
                        .comEmpresaValida()
                        .build()
        );

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comEmail(emailDuplicado)
                .comCpf()
                .comCracha()
                .comGrupoValido()
                .comEmpresaValida().build();

        enviarRequisicaoEValidarErro(novoAlunoRequest, 409, "Duplicated Resource", "Este e-mail já está em uso por outro aluno.");
        validarQuantidadeDeAlunosNoBancoPorCampo("email", emailDuplicado);
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com cpf duplicado.")
    public void cadastro_com_cpf_em_uso() {
        String cpfDuplicado = "42692470885";
        alunoTestFactory.salvarNovoAluno(testDataBuilder
                .comCpf(cpfDuplicado)
                .comGrupoValido()
                .comEmpresaValida()
                .build()
        );

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comCpf(cpfDuplicado).comCracha().comEmail().build();

        enviarRequisicaoEValidarErro(novoAlunoRequest, 409, "Duplicated Resource", "Este CPF já está em uso por outro aluno.");
        validarQuantidadeDeAlunosNoBancoPorCampo("cpf", cpfDuplicado);
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno com crachá duplicado.")
    public void cadastro_com_cracha_em_uso() {
        String crachaDuplicado = "15724";
        alunoTestFactory.salvarNovoAluno(testDataBuilder
                .comCracha(crachaDuplicado)
                .comGrupoValido()
                .comEmpresaValida()
                .build()
        );

        RequestCadastrarAlunoDto novoAlunoRequest = testDataBuilder
                .comCracha(crachaDuplicado)
                .comCpf()
                .comEmail()
                .comGrupoValido()
                .comEmpresaValida()
                .build();

        enviarRequisicaoEValidarErro(novoAlunoRequest, 409, "Duplicated Resource", "Este crachá já está em uso por outro aluno.");
        validarQuantidadeDeAlunosNoBancoPorCampo("cracha", crachaDuplicado);
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno informando um id de empresa não existente")
    public void cadastro_com_empresa_id_nao_existente() {
        RequestCadastrarAlunoDto request = testDataBuilder
                .comGrupoValido()
                .comEmpresaIdInvalido()
                .build();

        enviarRequisicaoEValidarErro(request, 404, "Not Found Resource", "Não foi encontrado uma empresa com o id informado.");
    }

    @Test
    @DisplayName("Não deve ser possível cadastrar um aluno informando um id de grupo não existente")
    public void cadastro_com_grupo_id_nao_existente() {
        RequestCadastrarAlunoDto request = testDataBuilder
                .comEmpresaValida()
                .comGrupoIdInvalido()
                .build();

        enviarRequisicaoEValidarErro(request, 404, "Not Found Resource", "Não foi encontrado um grupo com o id informado.");
    }

    private void enviarRequisicaoEValidarErro(RequestCadastrarAlunoDto request, int statusHttp, String tituloErro, String mensagemEsperada) {
        client.post().uri("/alunos")
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(statusHttp)
                .expectBody()
                .jsonPath("$.status").isEqualTo(statusHttp)
                .jsonPath("$.error").isEqualTo(tituloErro)
                .jsonPath("$.message").isEqualTo(mensagemEsperada);
    }

    private void validarQuantidadeDeAlunosNoBancoPorCampo(String coluna, String valor) {
        String sql = String.format("SELECT COUNT(*) FROM alunos WHERE %s = ?", coluna);
        Integer quantidadeNoDb = this.jdbcTemplate.queryForObject(sql, Integer.class, valor);
        Assertions.assertEquals(1, quantidadeNoDb,
                "A quantidade de alunos no banco não confere para a regra testada.");
    }
}