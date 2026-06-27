package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.dto.MargemOutput;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.factories.ServidorFactory;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ConsultarMargemUseCaseTest {

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private ConsultarMargemUseCase useCase;

    @Test
    @DisplayName("Cenário 1: Deve retornar a margem disponível corretamente para um servidor ativo")
    void deveRetornarMargemParaServidorAtivo() {
        // Arrange
        Long servidorId = 1L;
        BigDecimal margemEsperada = BigDecimal.valueOf(1500.00);
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setMargemConsignavel(margemEsperada);
        servidor.setAtivo(true);

        when(servidorRepository.findById(servidorId)).thenReturn(Optional.of(servidor));

        // Act
        MargemOutput output = useCase.execute(servidorId);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.margemDisponivel()).isEqualByComparingTo(margemEsperada);
    }

    @Test
    @DisplayName("Cenário 2: Deve lançar exceção se o servidor não existir")
    void deveLancarExcecaoQuandoServidorNaoExistir() {
        // Arrange
        Long servidorId = 99L;
        when(servidorRepository.findById(servidorId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(servidorId))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Servidor não encontrado");
    }
}
