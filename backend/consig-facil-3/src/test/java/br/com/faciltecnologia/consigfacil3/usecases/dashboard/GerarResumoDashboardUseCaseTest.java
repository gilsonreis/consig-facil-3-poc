package br.com.faciltecnologia.consigfacil3.usecases.dashboard;

import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.DashboardResumoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.EvolucaoMensalOutput;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.EvolucaoMensalProjection;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.DashboardInput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GerarResumoDashboardUseCaseTest {

    @Mock
    private ServidorRepository servidorRepository;

    @Mock
    private ContratoRepository contratoRepository;

    @InjectMocks
    private GerarResumoDashboardUseCase useCase;

    @Test
    @DisplayName("Deve gerar resumo do dashboard com sucesso")
    void deveGerarResumoDashboardComSucesso() {
        // Arrange
        when(servidorRepository.countByAtivoTrue()).thenReturn(10L);
        when(contratoRepository.countByStatus(StatusContrato.AVERBADO)).thenReturn(5L);
        when(contratoRepository.sumValorTotalByStatus(StatusContrato.AVERBADO)).thenReturn(BigDecimal.valueOf(50000));

        EvolucaoMensalProjection projection = mock(EvolucaoMensalProjection.class);
        when(projection.getMes()).thenReturn(6);
        when(projection.getAno()).thenReturn(2026);
        when(projection.getTotal()).thenReturn(BigDecimal.valueOf(10000));

        when(contratoRepository.findEvolucaoMensal(eq(StatusContrato.AVERBADO), any(LocalDateTime.class)))
                .thenReturn(List.of(projection));

        // Act
        DashboardResumoOutput output = useCase.execute(new DashboardInput());

        // Assert
        assertThat(output.totalServidoresAtivos()).isEqualTo(10L);
        assertThat(output.contratosAverbados()).isEqualTo(5L);
        assertThat(output.volumeEmprestado()).isEqualByComparingTo(BigDecimal.valueOf(50000));
        assertThat(output.graficoEvolucao()).hasSize(1);
        assertThat(output.graficoEvolucao().get(0).mesAno()).isEqualTo("06/2026");
        assertThat(output.graficoEvolucao().get(0).valor()).isEqualByComparingTo(BigDecimal.valueOf(10000));
    }

    @Test
    @DisplayName("Deve tratar valores nulos quando não houver contratos")
    void deveTratarValoresNulosQuandoNaoHouverContratos() {
        // Arrange
        when(servidorRepository.countByAtivoTrue()).thenReturn(0L);
        when(contratoRepository.countByStatus(StatusContrato.AVERBADO)).thenReturn(0L);
        when(contratoRepository.sumValorTotalByStatus(StatusContrato.AVERBADO)).thenReturn(null);
        when(contratoRepository.findEvolucaoMensal(eq(StatusContrato.AVERBADO), any(LocalDateTime.class)))
                .thenReturn(Collections.emptyList());

        // Act
        DashboardResumoOutput output = useCase.execute(new DashboardInput());

        // Assert
        assertThat(output.totalServidoresAtivos()).isZero();
        assertThat(output.contratosAverbados()).isZero();
        assertThat(output.volumeEmprestado()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(output.graficoEvolucao()).isEmpty();
    }
}
