package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.entities.HistoricoContrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.factories.ServidorFactory;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.EmprestimoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SolicitarEmprestimoUseCaseTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private HistoricoContratoRepository historicoContratoRepository;

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private SolicitarEmprestimoUseCase useCase;

    @Test
    @DisplayName("Deve solicitar empréstimo com sucesso quando dados são válidos e há margem")
    void deveSolicitarEmprestimoComSucesso() {
        // Arrange
        Long servidorId = 1L;
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setMargemConsignavel(BigDecimal.valueOf(1000.00));
        
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorId,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        when(servidorRepository.findById(servidorId)).thenReturn(Optional.of(servidor));
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(invocation -> {
            Contrato c = invocation.getArgument(0);
            return Contrato.builder()
                    .id(1L)
                    .servidor(c.getServidor())
                    .valorSolicitado(c.getValorSolicitado())
                    .taxaJurosMes(c.getTaxaJurosMes())
                    .quantidadeParcelas(c.getQuantidadeParcelas())
                    .valorParcela(c.getValorParcela())
                    .valorTotalFinanciado(c.getValorTotalFinanciado())
                    .status(c.getStatus())
                    .parcelas(c.getParcelas())
                    .build();
        });

        // Act
        EmprestimoOutput output = useCase.execute(input);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.contratoId()).isEqualTo(1L);
        assertThat(output.status()).isEqualTo(StatusContrato.DIGITADO);
        assertThat(output.valorParcela()).isNotNull();
        
        // Cálculo da Tabela Price conferido:
        // P = 10000, i = 0.02, n = 12
        // Parcela = 10000 * (0.02 * (1.02)^12) / ((1.02)^12 - 1) = 945.60
        assertThat(output.valorParcela()).isEqualByComparingTo("945.60");

        verify(contratoRepository).save(any(Contrato.class));
        verify(historicoContratoRepository).save(any(HistoricoContrato.class));
    }

    @Test
    @DisplayName("Deve lançar exceção quando margem for insuficiente")
    void deveLancarExcecaoMargemInsuficiente() {
        // Arrange
        Long servidorId = 1L;
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setMargemConsignavel(BigDecimal.valueOf(500.00));
        
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorId,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        when(servidorRepository.findById(servidorId)).thenReturn(Optional.of(servidor));

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Margem insuficiente");

        verify(contratoRepository, never()).save(any());
    }

    @Test
    @DisplayName("Deve lançar exceção quando servidor não for encontrado")
    void deveLancarExcecaoServidorNaoEncontrado() {
        // Arrange
        Long servidorId = 1L;
        when(servidorRepository.findById(servidorId)).thenReturn(Optional.empty());
        
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorId,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        // Act & Assert
        assertThatThrownBy(() -> useCase.execute(input))
                .isInstanceOf(RegraNegocioException.class)
                .hasMessageContaining("Servidor não encontrado");
    }
}
