package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.ServidorException;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CriarServidorInput;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CriarServidorUseCase {
    private final ServidorRepository servidorRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ServidorOutput execute(CriarServidorInput input) {
        if (servidorRepository.existsByMatricula(input.matricula())) {
            throw new ServidorException("Matrícula já cadastrada");
        }
        Usuario usuario = usuarioRepository.findById(input.usuarioId())
                .orElseThrow(() -> new ServidorException("Usuário não encontrado"));
        
        Servidor servidor = Servidor.builder()
                .usuario(usuario)
                .matricula(input.matricula())
                .margemConsignavel(input.margemConsignavel())
                .build();
        
        return ServidorOutput.fromEntity(servidorRepository.save(servidor));
    }
}
