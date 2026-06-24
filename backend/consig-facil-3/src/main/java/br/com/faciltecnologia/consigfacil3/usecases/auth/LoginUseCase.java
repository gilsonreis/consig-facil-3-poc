package br.com.faciltecnologia.consigfacil3.usecases.auth;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.AuthException;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.service.TokenService;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginInput;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoginUseCase {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final TokenService tokenService;

    public LoginOutput execute(LoginInput input) {
        // Regra de Login Triplo: busca por CPF, Email ou Username
        Usuario usuario = usuarioRepository.findByCpfOrEmailOrUsername(
                input.identificador(),
                input.identificador(),
                input.identificador()
        ).orElseThrow(() -> new AuthException("Usuário não encontrado ou inativo."));

        // O Spring Security usa o CPF como username principal (definido no método getUsername() da entidade)
        var authenticationToken = new UsernamePasswordAuthenticationToken(usuario.getCpf(), input.senha());

        Authentication authentication = authenticationManager.authenticate(authenticationToken);
        String token = tokenService.gerarToken((Usuario) authentication.getPrincipal());

        return new LoginOutput(token);
    }
}
