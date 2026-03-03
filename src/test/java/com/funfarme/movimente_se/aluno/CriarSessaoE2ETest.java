package com.funfarme.movimente_se.aluno;

import com.funfarme.movimente_se.BaseE2ETest;
import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;
import com.funfarme.movimente_se.dto.RequestLoginDto;
import com.funfarme.movimente_se.dto.ResponseLoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import org.mockito.Mockito;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class CriarSessaoE2ETest extends BaseE2ETest {
    private final String emailAluno = "aluno.teste@mail.com";
    private final String senhaAluno = "senha-aluno";

    @BeforeEach
    public void criarAlunoPadrao() {
        AlunoTestDataBuilder testDataBuilder = new AlunoTestDataBuilder(this.jdbcTemplate, this.clock);
        RequestCadastrarAlunoDto aluno = testDataBuilder
                .comEmail(this.emailAluno)
                .comSenha(this.senhaAluno)
                .build();
        testDataBuilder.salvarNoBanco(aluno);
    }

    @Test
    @DisplayName("Não deve ser possível realizar login com um email inválido.")
    public void login_com_email_invalido() {
        client.post().uri("/alunos/sessoes")
                .body(new RequestLoginDto(
                        "email.invalido@mail.com",
                        this.senhaAluno
                ))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("E-mail ou senha incorretos.");
    }

    @Test
    @DisplayName("Não deve ser possível realizar login com a senha incorreta.")
    public void login_com_senha_incorreta() {
        client.post().uri("/alunos/sessoes")
                .body(new RequestLoginDto(
                        this.emailAluno,
                        "senha-incorreta"
                ))
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.message").isEqualTo("E-mail ou senha incorretos.");
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token com expiração de 30 dias")
    void com_as_credenciais_corretas() {
        Instant agoraMockado = Instant.now();
        Mockito.when(this.clock.instant()).thenReturn(agoraMockado);
        Mockito.when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());

        long expiracaoEsperadaEmMillis =
                agoraMockado.plus(30, ChronoUnit.DAYS).toEpochMilli();
        ResponseLoginDto responseBody;

        EntityExchangeResult<ResponseLoginDto> response = client.post()
                .uri("/alunos/sessoes")
                .body(new RequestLoginDto(emailAluno, senhaAluno))
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseLoginDto.class)
                .returnResult();

        responseBody = response.getResponseBody();


        assertAll("Validação do login",
                () -> assertEquals(96, responseBody.access_token().length(), "O token deveria ter 96 caracteres"),
                () -> assertEquals(expiracaoEsperadaEmMillis, responseBody.expires_at(), "A expiração deve ser exatamente 30 dias a partir do login"),
                () -> assertNotNull(response.getResponseCookies().get("session_id"))
        );

        ResponseCookie sessionCookie = response.getResponseCookies().getFirst("session_id");
        assertNotNull(sessionCookie);
        assertTrue(sessionCookie.isHttpOnly());
        assertEquals("/", sessionCookie.getPath(), "O path do session_id está incorreto");
        assertEquals(Duration.ofDays(30), sessionCookie.getMaxAge(), "O max age está incorreto");
    }
}
