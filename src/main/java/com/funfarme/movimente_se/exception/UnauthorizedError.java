package com.funfarme.movimente_se.exception;

public class UnauthorizedError extends RuntimeException {
    public UnauthorizedError() {
        super("Não autorizado à acessar o recurso.");
    }

    public UnauthorizedError(String message) {
        super(message);
    }

    public String getName() {
        return "Unauthorized";
    }

    public int getStatus() {
        return 401;
    }
}