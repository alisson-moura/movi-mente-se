package com.funfarme.movimente_se;

import org.jspecify.annotations.NullMarked;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.ResponseCookie;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;
import java.util.Objects;

public class SessaoAluno {
    public long id;
    public long aluno_id;
    public String token;
    public Instant expira_em;
    public Instant criada_em;

    public static SessaoAluno nova(JdbcTemplate jdbc, Clock clock, long aluno_id) {
        SessaoAluno sessao = new SessaoAluno();
        sessao.aluno_id = aluno_id;
        sessao.token = SessaoAluno.gerarToken();
        sessao.expira_em = Instant.now(clock).plus(30, ChronoUnit.DAYS);
        sessao.criada_em = Instant.now(clock);

        KeyHolder keyHolder = new GeneratedKeyHolder();
        String sql = """
                INSERT INTO sessoes_alunos(aluno_id, token, expira_em, criado_em) VALUES(?, ?, ?, ?);
                """;
        jdbc.update(
                new PreparedStatementCreator() {
                    @NullMarked
                    public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
                        PreparedStatement ps = con.prepareStatement(sql, new String[]{"id"});
                        ps.setLong(1, aluno_id);
                        ps.setString(2, sessao.token);
                        ps.setTimestamp(3, Timestamp.from(sessao.expira_em));
                        ps.setTimestamp(4, Timestamp.from(sessao.criada_em));
                        return ps;
                    }
                },
                keyHolder
        );

        sessao.id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        return sessao;
    }

    public static SessaoAluno buscarPorToken(JdbcTemplate jdbc, Clock clock, String token) {
        String sql = "SELECT id, aluno_id, token, expira_em, criado_em FROM sessoes_alunos WHERE token = ?";
        try {
            SessaoAluno sessao = new SessaoAluno();
            jdbc.queryForObject(sql, (rs, rowNum) -> {
                sessao.id = rs.getLong("id");
                sessao.aluno_id = rs.getLong("aluno_id");
                sessao.token = rs.getString("token");
                sessao.expira_em = rs.getTimestamp("expira_em").toInstant();
                sessao.criada_em = rs.getTimestamp("criado_em").toInstant();
                return sessao;
            }, token);

            if (sessao.estaExpirada(clock)) return null;

            return sessao;
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public static String gerarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public ResponseCookie getCookie(Clock clock, String env) {
        return ResponseCookie
                .from("session_id")
                .value(this.token)
                .path("/")
                .httpOnly(true)
                .maxAge(Duration.between(Instant.now(clock), this.expira_em))
                .secure(env.equals("production"))
                .build();
    }

    private boolean estaExpirada(Clock clock) {
        return Instant.now(clock).isAfter(this.expira_em);
    }

    public void renovar(JdbcTemplate jdbc, Clock clock) {
        this.expira_em = Instant.now(clock).plus(30, ChronoUnit.DAYS);
        String sql = """
                UPDATE sessoes_alunos SET expira_em = ? WHERE id = ?;
                """;

        jdbc.update(
                sql,
                Timestamp.from(this.expira_em),
                this.id
        );
    }
}
