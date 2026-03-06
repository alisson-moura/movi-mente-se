package com.funfarme.movimente_se.entity;

import com.funfarme.movimente_se.dto.RequestCadastrarAlunoDto;

import java.time.LocalDate;
import java.util.UUID;

public class Aluno {
    private final String id;
    private String nome;
    private String sobrenome;
    private String email;
    private Senha senha;
    private String cpf;
    private String cracha;
    private String telefone;
    private String genero;
    private LocalDate dataNascimento;
    private boolean ativo;
    private long empresaId;
    private long grupoId;

    public Aluno(String id, String nome, String sobrenome, String email, Senha senha, String cpf, String cracha, String telefone, String genero, LocalDate dataNascimento, boolean ativo, long empresaId, long grupoId) {
        this.id = id;
        this.nome = nome;
        this.sobrenome = sobrenome;
        this.email = email;
        this.senha = senha;
        this.cpf = cpf;
        this.cracha = cracha;
        this.telefone = telefone;
        this.genero = genero;
        this.dataNascimento = dataNascimento;
        this.ativo = ativo;
        this.empresaId = empresaId;
        this.grupoId = grupoId;
    }

    public static Aluno novoAluno(RequestCadastrarAlunoDto dto) {
        UUID uuid = UUID.randomUUID();
        Senha senha = Senha.criarDeSenhaAberta(dto.senha());

        return new Aluno(
                uuid.toString(),
                dto.nome(),
                dto.sobrenome(),
                dto.email(),
                senha,
                dto.cpf(),
                dto.cracha(),
                dto.telefone(),
                dto.genero(),
                dto.data_nascimento(),
                true,
                dto.empresa_id(),
                dto.grupo_id()
        );
    }

    public String getId() {
        return id;
    }

    public String getNome() {
        return nome;
    }

    public String getSobrenome() {
        return sobrenome;
    }

    public String getEmail() {
        return email;
    }

    public Senha getSenha() {
        return senha;
    }

    public String getCpf() {
        return cpf;
    }

    public String getCracha() {
        return cracha;
    }

    public String getTelefone() {
        return telefone;
    }

    public String getGenero() {
        return genero;
    }

    public LocalDate getDataNascimento() {
        return dataNascimento;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public long getEmpresaId() {
        return empresaId;
    }

    public long getGrupoId() {
        return grupoId;
    }
}
