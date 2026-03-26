package com.amdev.movimente_se.aluno;

import com.amdev.movimente_se.dto.RequestCadastrarAlunoDto;
import net.datafaker.Faker;
import org.springframework.jdbc.core.JdbcTemplate;

import java.time.LocalDate;
import java.util.Locale;

public class AlunoTestDataBuilder {
    private final JdbcTemplate jdbc;
    private final Faker faker = new Faker(Locale.forLanguageTag("pt-BR"));

    private final String nome = faker.name().firstName();
    private final String sobrenome = faker.name().lastName();
    private String email = faker.internet().emailAddress();
    private String senha = faker.credentials().password();
    private String cpf = faker.cpf().valid(false);
    private String cracha = faker.numerify("#####");
    private final String telefone = faker.phoneNumber().cellPhone();
    private final String genero = faker.options().option("M", "F", "O");
    private final LocalDate dataNascimento = this.faker.timeAndDate().birthday(14, 90);
    private Integer empresaId;
    private Integer groupId;

    public AlunoTestDataBuilder(JdbcTemplate jdbc) {
        this.jdbc = jdbc;
    }

    public AlunoTestDataBuilder comCpf(String cpf) {
        this.cpf = cpf;
        return this;
    }

    public AlunoTestDataBuilder comCpf() {
        this.cpf = faker.cpf().valid(false);
        return this;
    }

    public AlunoTestDataBuilder comEmail(String email) {
        this.email = email;
        return this;
    }

    public AlunoTestDataBuilder comEmail() {
        this.email = faker.internet().emailAddress();
        return this;
    }

    public AlunoTestDataBuilder comSenha(String senhaPura) {
        this.senha = senhaPura;
        return this;
    }

    public AlunoTestDataBuilder comCracha(String cracha) {
        this.cracha = cracha;
        return this;
    }

    public AlunoTestDataBuilder comCracha() {
        this.cracha = faker.numerify("#####");
        ;
        return this;
    }

    public AlunoTestDataBuilder comEmpresaIdInvalido() {
        this.empresaId = 0;
        return this;
    }

    public AlunoTestDataBuilder comGrupoIdInvalido() {
        this.groupId = 0;
        return this;
    }

    public AlunoTestDataBuilder comEmpresaValida() {
        empresaId = this.jdbc.queryForObject("SELECT id FROM empresas LIMIT 1", Integer.class);
        return this;
    }

    public AlunoTestDataBuilder comGrupoValido() {
        groupId = this.jdbc.queryForObject("SELECT id FROM grupos LIMIT 1", Integer.class);
        return this;
    }

    public RequestCadastrarAlunoDto build() {
        return new RequestCadastrarAlunoDto(
                nome, sobrenome, email, senha, cpf, cracha,
                telefone, genero, dataNascimento, empresaId, groupId
        );
    }

}
