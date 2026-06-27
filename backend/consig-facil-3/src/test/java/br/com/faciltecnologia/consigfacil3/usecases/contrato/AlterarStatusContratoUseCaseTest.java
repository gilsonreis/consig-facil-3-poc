package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.entities.HistoricoContrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.AlterarStatusContratoInput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.AlterarStatusContratoOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AlterarStatusContratoUseCaseTest {

    @Mock
    private ContratoRepository contratoRepository;

    @Mock
    private HistoricoContratoRepository historicoContratoRepository;

    @InjectMocks
    private AlterarStatusContratoUseCase useCase;

    @Test
    @DisplayName("Cenário 1: Ao mudar de DIGITADO para AVERBADO, deve atualizar o status da Entidade e gravar histórico")
    void deveAlterarStatusEGravasHistorico() {
        // Arrange
        Long contratoId = 1L;
        Contrato contrato = Contrato.builder()
                .id(contratoId)
                .status(StatusContrato.DIGITADO)
                .build();

        AlterarStatusContratoInput input = new AlterarStatusContratoInput(
                contratoId,
                StatusContrato.AVERBADO,
                "Averbação confirmada"
        );

        when(contratoRepository.findById(contratoId)).thenReturn(Optional.of(contrato));
        when(contratoRepository.save(any(Contrato.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        AlterarStatusContratoOutput output = useCase.execute(input);

        // Assert
        assertThat(output).isNotNull();
        assertThat(output.novoStatus()).isEqualTo(StatusContrato.AVERBADO);

        verify(contratoRepository).save(contrato);
        
        ArgumentCaptor<HistoricoContrato> historicoCaptor = ArgumentCaptor.forClass(HistoricoContrato.class);
        verify(historicoContratoRepository).save(historicoCaptor.capture());
        
        HistoricoContrato historico = historicoCaptor.getValue();
        assertThat(historico.getStatusAnterior()).isEqualTo(StatusContrato.DIGITADO);
        assertThat(historico.getStatusNovo()).isEqualTo(StatusContrato.AVERBADO);
        assertThat(historico.getObservacao()).isEqualTo("Averbação confirmada");
    }
}
