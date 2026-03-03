package com.funfarme.movimente_se.controller;

import com.funfarme.movimente_se.Aluno;
import com.funfarme.movimente_se.SessaoAluno;
import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;
import com.funfarme.movimente_se.dto.RequestLoginDto;
import com.funfarme.movimente_se.dto.ResponseLoginDto;
import com.funfarme.movimente_se.exception.UnauthorizedError;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

@RestController
@RequestMapping("/alunos")
public class AlunosController {

    @Value("${spring.profiles.active}")
    private String environment;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final Clock clock;

    public AlunosController(Clock clock) {
        this.clock = clock;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrar(@RequestBody RequestCadastrarAlunoDto body) {
        Aluno.cadastrar(this.jdbcTemplate, body);
    }

    @PostMapping("sessoes")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody RequestLoginDto body) {
        Aluno aluno = Aluno.procurarPorEmail(this.jdbcTemplate, body.email());

        if (aluno == null || !aluno.senha.confereCom(body.senha()))
            throw new UnauthorizedError("E-mail ou senha incorretos.");

        SessaoAluno sessao = SessaoAluno.nova(
                this.jdbcTemplate,
                this.clock,
                aluno.id
        );

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        sessao.getCookie(this.clock, this.environment).toString()
                )
                .body(new ResponseLoginDto(
                        sessao.token,
                        sessao.expira_em.toEpochMilli()
                ));
    }

    @GetMapping("sessoes")
    public ResponseEntity<ResponseLoginDto> buscarSessao(@CookieValue("session_id") String sessionId) {
        SessaoAluno sessao = SessaoAluno.buscarPorToken(this.jdbcTemplate, this.clock, sessionId);

        if (sessao == null)
            throw new UnauthorizedError("Token invalido");

        sessao.renovar(this.jdbcTemplate, this.clock);

        return ResponseEntity.
                ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        sessao.getCookie(this.clock, this.environment).toString()
                )
                .body(new ResponseLoginDto(
                        sessao.token,
                        sessao.expira_em.toEpochMilli()
                ));
    }
}
