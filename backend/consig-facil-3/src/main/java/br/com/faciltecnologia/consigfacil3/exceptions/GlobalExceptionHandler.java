package br.com.faciltecnologia.consigfacil3.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Captura falhas de domínio no módulo de Autenticação.
     * Retorna HTTP 401 Unauthorized.
     */
    @ExceptionHandler(AuthException.class)
    public ResponseEntity<StandardError> authException(AuthException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                HttpStatus.UNAUTHORIZED.value(),
                "Falha de Autenticação",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Captura falhas de domínio no módulo de Servidores.
     * Retorna HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(ServidorException.class)
    public ResponseEntity<StandardError> servidorException(ServidorException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Unprocessable Entity",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Captura violações de regras de negócio.
     * Retorna HTTP 422 Unprocessable Entity.
     */
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<StandardError> regraNegocioException(RegraNegocioException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                HttpStatus.UNPROCESSABLE_ENTITY.value(),
                "Violação de Regra de Negócio",
                e.getMessage(),
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(error);
    }

    /**
     * Captura falhas de autenticação (Login) do Spring Security.
     * Retorna HTTP 401 Unauthorized.
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<StandardError> badCredentials(BadCredentialsException e, HttpServletRequest request) {
        StandardError error = new StandardError(
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                "Credenciais inválidas. Verifique seu identificador e senha.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    /**
     * Captura erros genéricos não mapeados.
     * Retorna HTTP 500 Internal Server Error com mensagem amigável.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<StandardError> generalException(Exception e, HttpServletRequest request) {
        StandardError error = new StandardError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Internal Server Error",
                "Ocorreu um erro interno no servidor. Por favor, tente novamente mais tarde.",
                request.getRequestURI()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
