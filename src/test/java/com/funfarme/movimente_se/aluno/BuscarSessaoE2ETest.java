package com.funfarme.movimente_se.aluno;

import com.funfarme.movimente_se.BaseE2ETest;
import com.funfarme.movimente_se.entity.Aluno;
import com.funfarme.movimente_se.entity.Sessao;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.client.ExchangeResult;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class BuscarSessaoE2ETest extends BaseE2ETest {
    private Sessao sessaoOriginal;

    @Autowired
    private AlunoTestFactory alunoTestFactory;

    @BeforeEach
    public void criarSessao() {
        AlunoTestDataBuilder testDataBuilder = new AlunoTestDataBuilder(this.jdbcTemplate);

        Aluno aluno = alunoTestFactory.salvarNovoAluno(testDataBuilder
                .comGrupoValido()
                .comEmpresaValida()
                .build()
        );
        this.sessaoOriginal = alunoTestFactory.novaSessaoPara(aluno);
    }

    @Test
    @DisplayName("Deve renovar a data de expiração da sessão com sucesso")
    void deveRenovarSessaoComTokenValido() {
        Instant agoraMockado = Instant.now().plus(15, ChronoUnit.DAYS);
        Mockito.when(this.clock.instant()).thenReturn(agoraMockado);
        Mockito.when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());

        ExchangeResult response = client.get()
                .uri("/alunos/sessoes")
                .header("Cookie", "session_id=" + sessaoOriginal.getToken())
                .exchange()
                .expectStatus().isOk()
                .returnResult();

        ResponseCookie sessionCookie = response.getResponseCookies().getFirst("session_id");

        assertNotNull(sessionCookie, "O cookie de sessão não foi retornado na resposta");
        assertTrue(sessionCookie.isHttpOnly(), "O cookie deve ter a flag HttpOnly ativada por motivos de segurança");
        assertEquals("/", sessionCookie.getPath(), "O escopo (path) do cookie deve ser a raiz da aplicação");
        assertEquals(Duration.ofDays(30), sessionCookie.getMaxAge(), "O max-age do cookie não foi renovado para 30 dias corretamente");
    }

    @Test
    @DisplayName("Não deve permitir renovar quando a sessão estiver expirada")
    void naoDeveRenovarComSessaoExpirada() {
        Instant agoraMockado = Instant.now().plus(31, ChronoUnit.DAYS);
        Mockito.when(this.clock.instant()).thenReturn(agoraMockado);
        Mockito.when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());

        client.get()
                .uri("/alunos/sessoes")
                .header("Cookie", "session_id=" + sessaoOriginal.getToken())
                .exchange()
                .expectStatus().isUnauthorized();
    }

    @Test
    @DisplayName("Não deve buscar ou renovar sessão com um token inexistente ou modificado")
    void naoDeveRenovarComTokenInvalido() {
        client.get()
                .uri("/alunos/sessoes")
                .header("Cookie", "session_id=token-invalido-123")
                .exchange()
                .expectStatus().isUnauthorized();
    }
}