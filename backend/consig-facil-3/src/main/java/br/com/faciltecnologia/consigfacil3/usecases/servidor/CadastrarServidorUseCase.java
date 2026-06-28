package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CadastrarServidorInput;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CadastrarServidorUseCase {

    private final ServidorRepository servidorRepository;
    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ServidorOutput execute(CadastrarServidorInput input) {
        if (servidorRepository.existsByMatricula(input.matricula())) {
            throw new RegraNegocioException("Matrícula já cadastrada");
        }

        Usuario usuario = usuarioRepository.findByCpf(input.cpf())
                .orElseGet(() -> {
                    Usuario novoUsuario = Usuario.builder()
                            .cpf(input.cpf())
                            .nome(input.nome())
                            .email(input.email())
                            .username(input.cpf())
                            .senha(passwordEncoder.encode(UUID.randomUUID().toString()))
                            .perfil(PerfilEnum.TOMADOR)
                            .ativo(true)
                            .build();
                    return usuarioRepository.save(novoUsuario);
                });

        Servidor servidor = Servidor.builder()
                .usuario(usuario)
                .matricula(input.matricula())
                .margemConsignavel(input.margemConsignavel())
                .ativo(true)
                .build();

        return ServidorOutput.fromEntity(servidorRepository.save(servidor));
    }
}
