package com.funfarme.movimente_se.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingRequestCookieException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(Exception ex) {
        ex.printStackTrace();

        ErrorResponseDto response = new ErrorResponseDto(
                "Internal Server Error",
                "Ocorreu um erro inesperado no servidor.",
                HttpStatus.INTERNAL_SERVER_ERROR.value()
        );

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }

    @ExceptionHandler(DuplicatedResourceError.class)
    public ResponseEntity<ErrorResponseDto> handleDuplicatedResource(DuplicatedResourceError ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                ex.getName(),
                ex.getMessage(),
                ex.getStatus()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(NotFoundResourceError.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundResource(NotFoundResourceError ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                ex.getName(),
                ex.getMessage(),
                ex.getStatus()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(UnauthorizedError.class)
    public ResponseEntity<ErrorResponseDto> handleUnauthorized(UnauthorizedError ex) {

        ErrorResponseDto response = new ErrorResponseDto(
                ex.getName(),
                ex.getMessage(),
                ex.getStatus()
        );

        return ResponseEntity.status(ex.getStatus()).body(response);
    }

    @ExceptionHandler(MissingRequestCookieException.class)
    public ResponseEntity<ErrorResponseDto> handleMissingCookie(MissingRequestCookieException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ErrorResponseDto(
                "Unauthorized",
                "Você precisa estar logado para acessar este recurso.",
                HttpStatus.UNAUTHORIZED.value()
        ));
    }
}