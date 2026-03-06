package com.funfarme.movimente_se.aluno;

import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;
import com.funfarme.movimente_se.entity.Aluno;
import com.funfarme.movimente_se.entity.Sessao;
import com.funfarme.movimente_se.repository.AlunoRepository;
import com.funfarme.movimente_se.repository.SessaoRepository;
import org.springframework.stereotype.Component;

import java.time.Clock;

@Component
public class AlunoTestFactory {

    private final AlunoRepository alunoRepository;
    private final SessaoRepository sessaoRepository;
    private final Clock clock;

    public AlunoTestFactory(AlunoRepository alunoRepository, SessaoRepository sessaoRepository, Clock clock) {
        this.alunoRepository = alunoRepository;
        this.sessaoRepository = sessaoRepository;
        this.clock = clock;
    }

    public Aluno salvarNovoAluno(RequestCadastrarAlunoDto request) {
        Aluno aluno = Aluno.novoAluno(request);
        alunoRepository.salvar(aluno);
        return aluno;
    }

    public Sessao novaSessaoPara(Aluno aluno) {
        Sessao sessao = new Sessao(this.clock, aluno.getId());
        sessaoRepository.salvar(sessao);
        return sessao;
    }
}