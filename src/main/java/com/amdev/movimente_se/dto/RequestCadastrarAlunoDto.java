package com.amdev.movimente_se.dto;

import java.time.LocalDate;

public record RequestCadastrarAlunoDto(
        String nome,
        String sobrenome,
        String email,
        String senha,
        String cpf,
        String cracha,
        String telefone,
        String genero,
        LocalDate data_nascimento,
        Integer empresa_id,
        Integer grupo_id
) {
}
