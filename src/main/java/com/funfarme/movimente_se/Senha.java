package com.funfarme.movimente_se;

import at.favre.lib.crypto.bcrypt.BCrypt;

public class Senha {
    private final String hash;

    private Senha(String hash) {
        this.hash = hash;
    }

    public static Senha criarDeSenhaAberta(String senhaDigitada) {
        if (senhaDigitada == null || senhaDigitada.isBlank()) {
            throw new IllegalArgumentException("A senha digitada não pode ser vazia.");
        }

        String hashGerado = BCrypt.withDefaults().hashToString(12, senhaDigitada.toCharArray());
        return new Senha(hashGerado);
    }

    public static Senha restaurarDeHash(String hashSalvoNoBanco) {
        if (hashSalvoNoBanco == null || hashSalvoNoBanco.isBlank()) {
            throw new IllegalArgumentException("O hash recuperado não pode ser vazio.");
        }
        return new Senha(hashSalvoNoBanco);
    }

    public String obterValorHash() {
        return this.hash;
    }

    public boolean confereCom(String senhaDigitadaParaTeste) {
        if (senhaDigitadaParaTeste == null || senhaDigitadaParaTeste.isBlank()) {
            return false;
        }

        BCrypt.Result resultado = BCrypt.verifyer().verify(senhaDigitadaParaTeste.toCharArray(), this.hash);
        return resultado.verified;
    }
}