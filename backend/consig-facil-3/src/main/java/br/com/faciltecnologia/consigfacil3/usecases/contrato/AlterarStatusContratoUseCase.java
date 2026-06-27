package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.entities.HistoricoContrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.AlterarStatusContratoInput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.AlterarStatusContratoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AlterarStatusContratoUseCase {

    private final ContratoRepository contratoRepository;
    private final HistoricoContratoRepository historicoContratoRepository;

    @Transactional
    public AlterarStatusContratoOutput execute(AlterarStatusContratoInput input) {
        Contrato contrato = contratoRepository.findById(input.contratoId())
                .orElseThrow(() -> new RegraNegocioException("Contrato não encontrado."));

        StatusContrato statusAnterior = contrato.getStatus();
        contrato.setStatus(input.novoStatus());

        Contrato contratoSalvo = contratoRepository.save(contrato);

        registrarHistorico(contratoSalvo, statusAnterior, input.novoStatus(), input.observacao());

        return new AlterarStatusContratoOutput(contratoSalvo.getId(), contratoSalvo.getStatus());
    }

    private void registrarHistorico(Contrato contrato, StatusContrato anterior, StatusContrato novo, String observacao) {
        HistoricoContrato historico = HistoricoContrato.builder()
                .contrato(contrato)
                .statusAnterior(anterior)
                .statusNovo(novo)
                .observacao(observacao)
                .build();
        historicoContratoRepository.save(historico);
    }
}
