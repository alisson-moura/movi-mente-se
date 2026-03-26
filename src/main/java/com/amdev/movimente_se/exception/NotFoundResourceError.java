package com.amdev.movimente_se.exception;

public class NotFoundResourceError extends RuntimeException {
    public NotFoundResourceError(String resource, String value) {
        super("Recurso " + resource + " com o valor " + value + " não foi encontrado.");
    }

    public NotFoundResourceError(String message) {
        super(message);
    }

    public String getName() {
        return "Not Found Resource";
    }

    public int getStatus() {
        return 404;
    }
}