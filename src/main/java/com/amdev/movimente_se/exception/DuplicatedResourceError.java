package com.amdev.movimente_se.exception;

public class DuplicatedResourceError extends RuntimeException {
    public DuplicatedResourceError(String resource, String value) {
        super("Recurso " + resource + " com o valor " + value + " já está em uso.");
    }

    public DuplicatedResourceError(String message) {
        super(message);
    }

    public String getName() {
        return "Duplicated Resource";
    }

    public int getStatus() {
        return 409;
    }
}