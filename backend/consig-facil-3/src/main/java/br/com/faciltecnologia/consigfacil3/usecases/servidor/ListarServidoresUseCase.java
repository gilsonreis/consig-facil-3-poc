package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
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
    public Page<ServidorResumoOutput> execute(ServidorFiltro filtro, Pageable pageable) {
        return servidorRepository.findResumoGroupedByCpf(filtro.termoBusca(), pageable);
    }
}
