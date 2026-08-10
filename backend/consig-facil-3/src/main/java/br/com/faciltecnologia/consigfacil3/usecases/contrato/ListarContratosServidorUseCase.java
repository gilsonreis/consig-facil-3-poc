package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.ContratoResumoOutput;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ListarContratosServidorUseCase {
    private final ContratoRepository contratoRepository;

    public Page<ContratoResumoOutput> execute(Long servidorId, Pageable pageable) {
        return contratoRepository.findByServidorId(servidorId, pageable)
                .map(c -> new ContratoResumoOutput(
                        c.getId(),
                        c.getValorTotalFinanciado(),
                        c.getValorParcela(),
                        c.getQuantidadeParcelas(),
                        c.getStatus().name(),
                        c.getDataSolicitacao()
                ));
    }
}
