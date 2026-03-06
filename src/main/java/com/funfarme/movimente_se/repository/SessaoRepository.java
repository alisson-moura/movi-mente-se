package com.funfarme.movimente_se.repository;

import com.funfarme.movimente_se.entity.Sessao;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.NullMarked;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.*;
import java.time.Clock;
import java.util.Optional;
import java.util.UUID;

@Repository
public class SessaoRepository {
    private final JdbcTemplate jdbc;
    private final Clock clock;

    public SessaoRepository(JdbcTemplate jdbcTemplate, Clock clock) {
        this.jdbc = jdbcTemplate;
        this.clock = clock;
    }

    public void salvar(Sessao sessao) {
        String sql = """
                 INSERT INTO sessoes
                    (token, usuario_id, data_expiracao, iniciada_em)
                 VALUES
                    (?, ?, ?, ?)
                """;

        jdbc.update(sql,
                sessao.getToken(),
                UUID.fromString(sessao.getUsuarioId()),
                Timestamp.from(sessao.getDataExpiracao()),
                Timestamp.from(sessao.getDataInicio())
        );
    }

    public void atualizar(Sessao sessao) {
        String sql = """
                UPDATE sessoes SET data_expiracao = ?, iniciada_em = ? WHERE token = ?;
                """;
        this.jdbc.update(sql,
                Timestamp.from(sessao.getDataExpiracao()),
                Timestamp.from(sessao.getDataInicio()),
                sessao.getToken()
        );
    }

    public Optional<Sessao> buscarPorToken(String token) {
        String sql = "SELECT * FROM sessoes WHERE token = ?";
        try {
            Sessao sessao = jdbc.queryForObject(sql,
                    new RowMapper<Sessao>() {
                        @Override
                        public Sessao mapRow(@NonNull ResultSet rs, int rowNum) throws SQLException {
                            return new Sessao(
                                    clock,
                                    rs.getString("usuario_id"),
                                    rs.getString("token"),
                                    rs.getTimestamp("data_expiracao").toInstant(),
                                    rs.getTimestamp("iniciada_em").toInstant()
                            );
                        }
                    },
                    token
            );
            return Optional.of(sessao);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }
}
