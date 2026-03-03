package com.funfarme.movimente_se;

import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;
import com.funfarme.movimente_se.exception.DuplicatedResourceError;
import com.funfarme.movimente_se.exception.NotFoundResourceError;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.*;
import java.util.Objects;

public class Aluno {
    public long id;
    public String nome;
    public String email;
    public Senha senha;

    public Aluno(
            long id,
            String email,
            Senha senha,
            String nome
    ) {
        this.id = id;
        this.email = email;
        this.senha = senha;
        this.nome = nome;
    }

    public static Aluno cadastrar(JdbcTemplate jdbc, RequestCadastrarAlunoDto novoAluno) {
        Integer emailEstaEmUso = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE email = ?",
                Integer.class,
                novoAluno.email()
        );
        if (emailEstaEmUso != null && emailEstaEmUso > 0)
            throw new DuplicatedResourceError("Este e-mail já está em uso por outro aluno.");

        Integer cpfEstaEmUso = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE cpf = ?",
                Integer.class,
                novoAluno.cpf()
        );
        if (cpfEstaEmUso != null && cpfEstaEmUso > 0)
            throw new DuplicatedResourceError("Este CPF já está em uso por outro aluno.");

        Integer crachaEstaEmUso = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE cracha = ?",
                Integer.class,
                novoAluno.cracha()
        );
        if (crachaEstaEmUso != null && crachaEstaEmUso > 0)
            throw new DuplicatedResourceError("Este crachá já está em uso por outro aluno.");

        Integer empresaExiste = jdbc.queryForObject(
                "SELECT COUNT(*) FROM empresas WHERE id = ? AND ativo = true",
                Integer.class,
                novoAluno.empresa_id()
        );
        if (empresaExiste == null || empresaExiste == 0)
            throw new NotFoundResourceError("Não foi encontrado uma empresa com o id informado.");

        Integer grupoExiste = jdbc.queryForObject(
                "SELECT COUNT(*) FROM grupos WHERE id = ? AND ativo = true",
                Integer.class,
                novoAluno.group_id()
        );
        if (grupoExiste == null || grupoExiste == 0)
            throw new NotFoundResourceError("Não foi encontrado um grupo com o id informado.");

        Senha senha = Senha.criarDeSenhaAberta(novoAluno.senha());

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = "INSERT INTO alunos(nome, sobrenome, email, senha, cpf, cracha, telefone, genero, data_nascimento, empresa_id, grupo_id) " +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        jdbc.update(
                new PreparedStatementCreator() {
                    @NullMarked
                    public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                        PreparedStatement ps = connection.prepareStatement(sql, new String[]{"id"});
                        ps.setString(1, novoAluno.nome());
                        ps.setString(2, novoAluno.sobrenome());
                        ps.setString(3, novoAluno.email());
                        ps.setString(4, senha.obterValorHash());
                        ps.setString(5, novoAluno.cpf());
                        ps.setString(6, novoAluno.cracha());
                        ps.setString(7, novoAluno.telefone());
                        ps.setString(8, novoAluno.genero());
                        ps.setDate(9, Date.valueOf(novoAluno.data_nascimento()));
                        ps.setLong(10, novoAluno.empresa_id());
                        ps.setLong(11, novoAluno.group_id());
                        return ps;
                    }
                },
                keyHolder);

        return new Aluno(
                Objects.requireNonNull(keyHolder.getKey()).longValue(),
                novoAluno.email(),
                senha,
                novoAluno.nome()
        );
    }

    public static Aluno procurarPorEmail(JdbcTemplate jdbc, String email) {
        String sql = "SELECT * FROM alunos WHERE email = ? AND ativo = true LIMIT 1";
        try {
            return jdbc.queryForObject(sql,
                    new RowMapper<Aluno>() {
                        @Override
                        public Aluno mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                            return new Aluno(
                                    rs.getInt("id"),
                                    rs.getString("email"),
                                    Senha.restaurarDeHash(rs.getString("senha")),
                                    rs.getString("nome")
                            );
                        }
                    },
                    email
            );
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }
}
