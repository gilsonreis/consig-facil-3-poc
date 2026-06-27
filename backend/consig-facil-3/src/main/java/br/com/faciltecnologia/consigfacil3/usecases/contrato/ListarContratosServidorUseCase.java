package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.ContratoResumoOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ListarContratosServidorUseCase {

    public Page<ContratoResumoOutput> execute(Long servidorId, Pageable pageable) {
        // TODO: Implementar listagem de contratos do servidor
        return Page.empty();
    }
}
