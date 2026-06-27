package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.SimulacaoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class SimularEmprestimoUseCase {

    public SimulacaoOutput execute(SolicitarEmprestimoInput input) {
        // TODO: Implementar cálculo usando Tabela Price
        return new SimulacaoOutput(BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
