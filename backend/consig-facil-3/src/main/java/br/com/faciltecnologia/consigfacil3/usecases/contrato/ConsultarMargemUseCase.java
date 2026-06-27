package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.MargemOutput;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;

@Service
public class ConsultarMargemUseCase {

    public MargemOutput execute(Long servidorId) {
        // TODO: Implementar lógica de consulta de margem
        return new MargemOutput(BigDecimal.ZERO);
    }
}
