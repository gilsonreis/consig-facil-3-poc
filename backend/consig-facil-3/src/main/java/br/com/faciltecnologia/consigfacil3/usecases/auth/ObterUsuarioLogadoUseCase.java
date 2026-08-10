package br.com.faciltecnologia.consigfacil3.usecases.auth;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.AuthException;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.ObterUsuarioLogadoInput;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.UsuarioPerfilOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObterUsuarioLogadoUseCase {

    private final UsuarioRepository usuarioRepository;

    public UsuarioPerfilOutput execute(ObterUsuarioLogadoInput input) {
        Usuario usuario = usuarioRepository.findByCpf(input.cpf())
                .orElseThrow(() -> new AuthException("Usuário não encontrado."));

        return new UsuarioPerfilOutput(
                usuario.getId(),
                usuario.getNome(),
                usuario.getCpf(),
                usuario.getEmail()
        );
    }
}
