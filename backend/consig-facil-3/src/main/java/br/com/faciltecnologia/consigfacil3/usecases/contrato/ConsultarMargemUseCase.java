package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.dto.MargemOutput;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ConsultarMargemUseCase {

    private final ServidorRepository servidorRepository;

    public MargemOutput execute(Long servidorId) {
        Servidor servidor = servidorRepository.findById(servidorId)
                .orElseThrow(() -> new RegraNegocioException("Servidor não encontrado."));

        return new MargemOutput(servidor.getMargemConsignavel());
    }
}
