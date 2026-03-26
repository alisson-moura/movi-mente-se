package com.amdev.movimente_se.controller;

import com.amdev.movimente_se.entity.Aluno;
import com.amdev.movimente_se.entity.Sessao;
import com.amdev.movimente_se.dto.RequestCadastrarAlunoDto;
import com.amdev.movimente_se.dto.RequestLoginDto;
import com.amdev.movimente_se.dto.ResponseLoginDto;
import com.amdev.movimente_se.exception.DuplicatedResourceError;
import com.amdev.movimente_se.exception.NotFoundResourceError;
import com.amdev.movimente_se.exception.UnauthorizedError;
import com.amdev.movimente_se.repository.AlunoRepository;
import com.amdev.movimente_se.repository.SessaoRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Clock;

@RestController
@RequestMapping("/alunos")
public class AlunosController {

    @Value("${spring.profiles.active}")
    private String environment;
    private final AlunoRepository alunoRepository;
    private final SessaoRepository sessaoRepository;
    private final Clock clock;

    public AlunosController(Clock clock, AlunoRepository alunoRepository, SessaoRepository sessaoRepository) {
        this.clock = clock;
        this.alunoRepository = alunoRepository;
        this.sessaoRepository = sessaoRepository;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void cadastrar(@RequestBody RequestCadastrarAlunoDto body) {
        Aluno aluno = Aluno.novoAluno(body);
        if (this.alunoRepository.cpfEmUsoPorOutroAluno(aluno.getId(), aluno.getCpf()))
            throw new DuplicatedResourceError("Este CPF já está em uso por outro aluno.");

        if (this.alunoRepository.crachaEmUsoPorOutroAluno(aluno.getId(), aluno.getCracha()))
            throw new DuplicatedResourceError("Este crachá já está em uso por outro aluno.");

        if (this.alunoRepository.procurarPorEmail(body.email()).isPresent())
            throw new DuplicatedResourceError("Este e-mail já está em uso por outro aluno.");

        if (!this.alunoRepository.existeEmpresaComId(body.empresa_id()))
            throw new NotFoundResourceError("Não foi encontrado uma empresa com o id informado.");

        if (!this.alunoRepository.existeGrupoComId(body.grupo_id()))
            throw new NotFoundResourceError("Não foi encontrado um grupo com o id informado.");

        this.alunoRepository.salvar(aluno);
    }

    @PostMapping("sessoes")
    public ResponseEntity<ResponseLoginDto> login(@RequestBody RequestLoginDto body) {
        Aluno aluno = alunoRepository.procurarPorEmail(body.email())
                .filter(Aluno::isAtivo)
                .filter(a -> a.getSenha().confereCom(body.senha()))
                .orElseThrow(() -> new UnauthorizedError("E-mail ou senha incorretos."));

        Sessao sessao = new Sessao(this.clock, aluno.getId());
        this.sessaoRepository.salvar(sessao);

        return ResponseEntity
                .ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        sessao.getCookie(this.environment).toString()
                )
                .body(new ResponseLoginDto(
                        sessao.getToken(),
                        sessao.getDataExpiracao().toEpochMilli()
                ));
    }

    @GetMapping("sessoes")
    public ResponseEntity<ResponseLoginDto> buscarSessao(@CookieValue("session_id") String sessionId) {
        Sessao sessao = this.sessaoRepository.buscarPorToken(sessionId)
                .filter(s -> !s.estaExpirada())
                .orElseThrow(() -> new UnauthorizedError("Token invalido"));

        sessao.renovar();
        this.sessaoRepository.atualizar(sessao);

        return ResponseEntity.
                ok()
                .header(
                        HttpHeaders.SET_COOKIE,
                        sessao.getCookie(this.environment).toString()
                )
                .body(new ResponseLoginDto(
                        sessao.getToken(),
                        sessao.getDataExpiracao().toEpochMilli()
                ));
    }
}