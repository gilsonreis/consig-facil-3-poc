package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.SimulacaoOutput;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SimularEmprestimoUseCaseTest {

    @InjectMocks
    private SimularEmprestimoUseCase useCase;

    @Test
    @DisplayName("Cenário 1: Deve calcular a parcela via Tabela Price corretamente")
    void deveCalcularParcelaCorretamente() {
        // Arrange
        // Simular 10.000,00 com 2% de juros em 12x
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                1L,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        // Act
        SimulacaoOutput output = useCase.execute(input);

        // Assert
        assertThat(output).isNotNull();
        // Parcela calculada para 10000, 2%, 12x = 945.60
        assertThat(output.valorParcela()).isEqualByComparingTo("945.60");
        assertThat(output.valorTotal()).isEqualByComparingTo("11347.20");
    }

    @Test
    @DisplayName("Cenário 2: Deve lançar erro de validação se tentar simular com prazo 0")
    void deveLancarExcecaoQuandoPrazoForZero() {
        // Arrange
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                1L,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                0
        );

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Quantidade de parcelas deve ser maior que zero");
    }

    @Test
    @DisplayName("Cenário 2: Deve lançar erro de validação se tentar simular com valor negativo")
    void deveLancarExcecaoQuandoValorForNegativo() {
        // Arrange
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                1L,
                BigDecimal.valueOf(-1000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Valor solicitado deve ser maior que zero");
    }
}
