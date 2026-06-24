package br.com.faciltecnologia.consigfacil3.service;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.utils.UsuarioFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class TokenServiceTest {

    private TokenService tokenService;
    private static final String SECRET_TEST = "9f8e7d6c5b4a3f2e1d0c9b8a7f6e5d4c3b2a1f0e";

    @BeforeEach
    void setUp() {
        tokenService = new TokenService();
        // Injeção da secret via reflexão, simulando o @Value do Spring
        ReflectionTestUtils.setField(tokenService, "secret", SECRET_TEST);
    }

    @Test
    @DisplayName("Deve embutir o CPF do usuário no subject do token")
    void deveGerarTokenComCpfNoSubject() {
        Usuario usuario = UsuarioFactory.criarUsuarioValido();
        
        String token = tokenService.gerarToken(usuario);
        
        assertThat(token).isNotBlank();
        String subject = tokenService.getSubject(token);
        assertThat(subject).isEqualTo(usuario.getCpf());
    }

    @Test
    @DisplayName("Deve extrair corretamente o CPF de um token válido")
    void deveExtrairCpfDeTokenValido() {
        Usuario usuario = UsuarioFactory.criarUsuarioValido();
        String token = tokenService.gerarToken(usuario);

        String subject = tokenService.getSubject(token);

        assertThat(subject).isEqualTo(usuario.getCpf());
    }

    @Test
    @DisplayName("Deve lançar exceção ao validar um token alterado ou inválido")
    void deveLancarExcecaoParaTokenInvalido() {
        String tokenInvalido = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.invalid.payload";

        assertThatThrownBy(() -> tokenService.getSubject(tokenInvalido))
                .isInstanceOf(RuntimeException.class);
    }
}
