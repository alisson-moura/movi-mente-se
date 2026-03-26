package com.amdev.movimente_se.aluno;

import com.amdev.movimente_se.BaseE2ETest;
import com.amdev.movimente_se.dto.RequestCadastrarAlunoDto;
import com.amdev.movimente_se.dto.RequestLoginDto;
import com.amdev.movimente_se.dto.ResponseLoginDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseCookie;
import org.springframework.test.web.servlet.client.EntityExchangeResult;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.*;

public class CriarSessaoE2ETest extends BaseE2ETest {

    private final String emailAluno = "aluno.teste@mail.com";
    private final String senhaAluno = "senha-aluno";

    @Autowired
    private AlunoTestFactory alunoTestFactory;

    @BeforeEach
    public void criarAlunoPadrao() {
        AlunoTestDataBuilder testDataBuilder = new AlunoTestDataBuilder(this.jdbcTemplate);

        RequestCadastrarAlunoDto aluno = testDataBuilder
                .comEmail(this.emailAluno)
                .comSenha(this.senhaAluno)
                .comEmpresaValida()
                .comGrupoValido()
                .build();

        alunoTestFactory.salvarNovoAluno(aluno);
    }

    @Test
    @DisplayName("Não deve ser possível realizar login com um email inválido.")
    public void login_com_email_invalido() {
        RequestLoginDto request = new RequestLoginDto("email.invalido@mail.com", this.senhaAluno);
        realizarLoginEValidarErro(request);
    }

    @Test
    @DisplayName("Não deve ser possível realizar login com a senha incorreta.")
    public void login_com_senha_incorreta() {
        RequestLoginDto request = new RequestLoginDto(this.emailAluno, "senha-incorreta");
        realizarLoginEValidarErro(request);
    }

    @Test
    @DisplayName("Deve realizar login com sucesso e retornar token com expiração de 30 dias")
    void login_com_credenciais_corretas() {
        Instant agoraMockado = Instant.now();
        Mockito.when(this.clock.instant()).thenReturn(agoraMockado);
        Mockito.when(this.clock.getZone()).thenReturn(ZoneId.systemDefault());

        long expiracaoEsperadaEmMillis = agoraMockado.plus(30, ChronoUnit.DAYS).toEpochMilli();
        RequestLoginDto request = new RequestLoginDto(this.emailAluno, this.senhaAluno);

        EntityExchangeResult<ResponseLoginDto> response = client.post()
                .uri("/alunos/sessoes")
                .body(request)
                .exchange()
                .expectStatus().isOk()
                .expectBody(ResponseLoginDto.class)
                .returnResult();

        ResponseLoginDto responseBody = response.getResponseBody();
        assertNotNull(responseBody, "O corpo da resposta não deve ser nulo");

        assertAll("Validação do payload de login",
                () -> assertEquals(96, responseBody.access_token().length(), "O token deveria ter 96 caracteres"),
                () -> assertEquals(expiracaoEsperadaEmMillis, responseBody.expires_at(), "A expiração deve ser exatamente 30 dias a partir do login")
        );

        ResponseCookie sessionCookie = response.getResponseCookies().getFirst("session_id");
        assertNotNull(sessionCookie, "O cookie session_id deve estar presente na resposta");

        assertAll("Validação do Cookie de Sessão",
                () -> assertTrue(sessionCookie.isHttpOnly(), "O cookie deve ser HttpOnly para segurança"),
                () -> assertEquals("/", sessionCookie.getPath(), "O path do session_id está incorreto"),
                () -> assertEquals(Duration.ofDays(30), sessionCookie.getMaxAge(), "O max age do cookie está incorreto")
        );
    }

    private void realizarLoginEValidarErro(RequestLoginDto request) {
        client.post().uri("/alunos/sessoes")
                .body(request)
                .exchange()
                .expectStatus().isEqualTo(401)
                .expectBody()
                .jsonPath("$.message").isEqualTo("E-mail ou senha incorretos.");
    }
}