package br.com.faciltecnologia.consigfacil3.exceptions;

import java.time.LocalDateTime;

/**
 * Record padronizado para respostas de erro da API.
 */
public record StandardError(
        LocalDateTime timestamp,
        Integer status,
        String error,
        String message,
        String path
) {
    // Construtor compacto para injetar o timestamp automaticamente
    public StandardError(Integer status, String error, String message, String path) {
        this(LocalDateTime.now(), status, error, message, path);
    }
}
