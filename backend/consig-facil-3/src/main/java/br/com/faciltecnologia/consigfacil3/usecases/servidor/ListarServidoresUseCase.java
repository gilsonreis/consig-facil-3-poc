package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ListarServidoresUseCase {
    private final ServidorRepository servidorRepository;

    public List<ServidorOutput> execute() {
        return servidorRepository.findAll().stream()
                .map(ServidorOutput::fromEntity)
                .toList();
    }
}
