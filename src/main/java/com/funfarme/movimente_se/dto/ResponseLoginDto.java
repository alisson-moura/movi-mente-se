package com.funfarme.movimente_se.dto;

public record ResponseLoginDto(
        String access_token,
        long expires_at
) {
}

