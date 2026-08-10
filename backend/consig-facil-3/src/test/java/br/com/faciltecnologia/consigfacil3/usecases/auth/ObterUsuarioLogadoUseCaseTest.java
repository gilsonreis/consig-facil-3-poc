package br.com.faciltecnologia.consigfacil3.usecases.auth;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.AuthException;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.ObterUsuarioLogadoInput;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.UsuarioPerfilOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ObterUsuarioLogadoUseCaseTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private ObterUsuarioLogadoUseCase useCase;

    @Test
    @DisplayName("Deve retornar dados do perfil do usuário quando encontrado por CPF")
    void deveRetornarPerfilUsuario() {
        // Arrange
        String cpf = "12345678901";
        ObterUsuarioLogadoInput input = new ObterUsuarioLogadoInput(cpf);
        Usuario usuario = Usuario.builder()
                .id(1L)
                .nome("Servidor Teste")
                .cpf(cpf)
                .email("servidor@teste.com")
                .build();

        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.of(usuario));

        // Act
        UsuarioPerfilOutput result = useCase.execute(input);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.id()).isEqualTo(1L);
        assertThat(result.nome()).isEqualTo("Servidor Teste");
        assertThat(result.cpf()).isEqualTo(cpf);
        assertThat(result.email()).isEqualTo("servidor@teste.com");
    }

    @Test
    @DisplayName("Deve lançar AuthException quando usuário não for encontrado")
    void deveLancarErroUsuarioNaoEncontrado() {
        // Arrange
        String cpf = "11111111111";
        ObterUsuarioLogadoInput input = new ObterUsuarioLogadoInput(cpf);

        when(usuarioRepository.findByCpf(cpf)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(AuthException.class)
                .hasMessage("Usuário não encontrado.");
    }
}
