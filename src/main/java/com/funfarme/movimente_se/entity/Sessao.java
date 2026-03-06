package com.funfarme.movimente_se.entity;

import org.springframework.http.ResponseCookie;

import java.security.SecureRandom;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HexFormat;

public class Sessao {
    private Clock clock;
    private String usuarioId;
    private String token;
    private Instant dataExpiracao;
    private Instant iniciadaEm;

    public Sessao(Clock clock, String usuarioId) {
        this.clock = clock;
        this.usuarioId = usuarioId;
        this.iniciadaEm = Instant.now(clock);
        this.dataExpiracao = this.iniciadaEm.plus(30, ChronoUnit.DAYS);
        this.token = this.gerarToken();
    }

    public Sessao(Clock clock, String usuarioId, String token, Instant dataExpiracao, Instant iniciadaEm) {
        this.clock = clock;
        this.usuarioId = usuarioId;
        this.token = token;
        this.dataExpiracao = dataExpiracao;
        this.iniciadaEm = iniciadaEm;
    }

    private String gerarToken() {
        SecureRandom random = new SecureRandom();
        byte[] bytes = new byte[48];
        random.nextBytes(bytes);
        return HexFormat.of().formatHex(bytes);
    }

    public ResponseCookie getCookie(String env) {
        return ResponseCookie
                .from("session_id")
                .value(this.token)
                .path("/")
                .httpOnly(true)
                .maxAge(Duration.between(Instant.now(clock), this.dataExpiracao))
                .secure(env.equals("production"))
                .build();
    }

    public String getToken() {
        return this.token;
    }

    public Instant getDataExpiracao() {
        return this.dataExpiracao;
    }

    public String getUsuarioId() {
        return this.usuarioId;
    }

    public Instant getDataInicio() {
        return this.iniciadaEm;
    }

    public boolean estaExpirada() {
        return this.dataExpiracao.isBefore(Instant.now(clock));
    }

    public void renovar() {
        Instant agora = Instant.now(clock);
        this.iniciadaEm = agora;
        this.dataExpiracao = agora.plus(30, ChronoUnit.DAYS);
    }
}
