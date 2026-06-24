package br.com.faciltecnologia.consigfacil3.exceptions;

/**
 * Exceção de domínio específica para falhas no módulo de Autenticação.
 */
public class AuthException extends RuntimeException {
    public AuthException(String message) {
        super(message);
    }
}
