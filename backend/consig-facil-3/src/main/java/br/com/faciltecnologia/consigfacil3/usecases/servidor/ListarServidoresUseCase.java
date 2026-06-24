package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.spec.ServidorSpecification;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ListarServidoresUseCase {
    private final ServidorRepository servidorRepository;

    @Transactional(readOnly = true)
    public Page<ServidorOutput> execute(ServidorFiltro filtro, Pageable pageable) {
        return servidorRepository.findAll(ServidorSpecification.comFiltro(filtro), pageable)
                .map(ServidorOutput::fromEntity);
    }
}
