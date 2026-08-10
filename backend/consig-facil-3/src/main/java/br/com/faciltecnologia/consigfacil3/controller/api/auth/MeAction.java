package br.com.faciltecnologia.consigfacil3.controller.api.auth;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.exceptions.AuthException;
import br.com.faciltecnologia.consigfacil3.usecases.auth.ObterUsuarioLogadoUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.ObterUsuarioLogadoInput;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.UsuarioPerfilOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class MeAction {

    private final ObterUsuarioLogadoUseCase obterUsuarioLogadoUseCase;

    @GetMapping("/me")
    public ResponseEntity<UsuarioPerfilOutput> execute(@AuthenticationPrincipal Usuario usuarioLogado) {
        if (usuarioLogado == null) {
            throw new AuthException("Usuário não autenticado.");
        }

        UsuarioPerfilOutput output = obterUsuarioLogadoUseCase.execute(new ObterUsuarioLogadoInput(usuarioLogado.getCpf()));
        return ResponseEntity.ok(output);
    }
}
