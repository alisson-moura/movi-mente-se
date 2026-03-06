package com.funfarme.movimente_se.repository;

import com.funfarme.movimente_se.entity.Senha;
import com.funfarme.movimente_se.entity.Aluno;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlunoRepository {
    private final JdbcTemplate jdbc;

    public AlunoRepository(JdbcTemplate jdbcTemplate) {
        this.jdbc = jdbcTemplate;
    }

    public void salvar(Aluno aluno) {
        String sql = """
                 INSERT INTO alunos
                    (id, nome, sobrenome, email, senha, cpf, cracha, telefone, genero, data_nascimento, empresa_id, grupo_id)
                 VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        jdbc.update(new PreparedStatementCreator() {
            @NullMarked
            public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                PreparedStatement ps = con.prepareStatement(sql);
                ps.setObject(1, UUID.fromString(aluno.getId()));
                ps.setString(2, aluno.getNome());
                ps.setString(3, aluno.getSobrenome());
                ps.setString(4, aluno.getEmail());
                ps.setString(5, aluno.getSenha().obterValorHash());
                ps.setString(6, aluno.getCpf());
                ps.setString(7, aluno.getCracha());
                ps.setString(8, aluno.getTelefone());
                ps.setString(9, aluno.getGenero());
                ps.setDate(10, Date.valueOf(aluno.getDataNascimento()));
                ps.setLong(11, aluno.getEmpresaId());
                ps.setLong(12, aluno.getGrupoId());
                return ps;
            }
        });
    }

    public Optional<Aluno> procurarPorEmail(String email) {
        String sql = """
                SELECT * FROM alunos
                WHERE email = ?
                LIMIT 1
                """;
        try {
            Aluno aluno = jdbc.queryForObject(sql,
                    new RowMapper<Aluno>() {
                        @Override
                        public Aluno mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                            Senha senha = Senha.restaurarDeHash(rs.getString("senha"));
                            LocalDate dataNascimento = rs.getDate("data_nascimento").toLocalDate();
                            return new Aluno(
                                    rs.getString("id"),
                                    rs.getString("nome"),
                                    rs.getString("sobrenome"),
                                    rs.getString("email"),
                                    senha,
                                    rs.getString("cpf"),
                                    rs.getString("cracha"),
                                    rs.getString("telefone"),
                                    rs.getString("genero"),
                                    dataNascimento,
                                    rs.getBoolean("ativo"),
                                    rs.getLong("empresa_id"),
                                    rs.getLong("grupo_id")
                            );
                        }
                    },
                    email
            );

            return Optional.of(aluno);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    public boolean cpfEmUsoPorOutroAluno(String id, String cpf) {
        Integer cpfEstaEmUso = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE id <> CAST(? AS UUID) AND cpf = ?",
                Integer.class,
                id,
                cpf
        );

        return cpfEstaEmUso != null && cpfEstaEmUso > 0;
    }

    public boolean crachaEmUsoPorOutroAluno(String id, String cracha) {
        Integer crachaEstaEmUso = jdbc.queryForObject(
                "SELECT COUNT(*) FROM alunos WHERE id <> CAST(? AS UUID) AND cracha = ?",
                Integer.class,
                id,
                cracha
        );

        return crachaEstaEmUso != null && crachaEstaEmUso > 0;
    }

    public boolean existeEmpresaComId(Integer empresaId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM empresas WHERE id = ?", Integer.class, empresaId
        );
        return count != null && count > 0;
    }

    public boolean existeGrupoComId(Integer grupoId) {
        Integer count = jdbc.queryForObject(
                "SELECT COUNT(*) FROM grupos WHERE id = ?", Integer.class, grupoId
        );
        return count != null && count > 0;
    }
}
