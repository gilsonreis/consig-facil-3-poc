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
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
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
    @DisplayName("Cenário 1: Sucesso - Servidor ativo e com margem suficiente")
    void deveSolicitarEmprestimoComSucesso() {
        // Arrange
        Long servidorId = 1L;
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setMargemConsignavel(BigDecimal.valueOf(1000.00));
        servidor.setAtivo(true);
        
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorId,
                BigDecimal.valueOf(10000.00),
                BigDecimal.valueOf(2.0),
                12
        );

        when(servidorRepository.findById(servidorId)).thenReturn(Optional.of(servidor));
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        EmprestimoOutput output = useCase.execute(input);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.status()).isEqualTo(StatusContrato.DIGITADO);
        
        ArgumentCaptor<Contrato> contratoCaptor = ArgumentCaptor.forClass(Contrato.class);
        verify(contratoRepository, times(1)).save(contratoCaptor.capture());
        
        Contrato contratoSalvo = contratoCaptor.getValue();
        assertThat(contratoSalvo.getParcelas()).hasSize(12);
        assertThat(contratoSalvo.getParcelas().get(0).getDataVencimento())
                .isEqualTo(LocalDate.now().plusMonths(1));

        verify(historicoContratoRepository, times(1)).save(any(HistoricoContrato.class));
        
        ArgumentCaptor<HistoricoContrato> historicoCaptor = ArgumentCaptor.forClass(HistoricoContrato.class);
        verify(historicoContratoRepository).save(historicoCaptor.capture());
        assertThat(historicoCaptor.getValue().getStatusNovo()).isEqualTo(StatusContrato.DIGITADO);
    }

    @Test
    @DisplayName("Cenário 2: Bloqueio de Margem - Servidor com margem insuficiente")
    void deveLancarExcecaoMargemInsuficiente() {
        // Arrange
        Long servidorId = 1L;
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setMargemConsignavel(BigDecimal.valueOf(500.00));
        servidor.setAtivo(true);
        
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
    @DisplayName("Cenário 3: Bloqueio de Inativo - Servidor inativo")
    void deveLancarExcecaoServidorInativo() {
        // Arrange
        Long servidorId = 1L;
        Servidor servidor = ServidorFactory.criarEntidadeValida(null);
        servidor.setId(servidorId);
        servidor.setAtivo(false);
        
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
                .hasMessageContaining("Servidor inativo");

        verify(contratoRepository, never()).save(any());
    }
}
