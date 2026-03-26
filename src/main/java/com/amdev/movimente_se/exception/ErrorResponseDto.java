package com.amdev.movimente_se.exception;

public record ErrorResponseDto(
        String error,
        String message,
        int status
) {
}
