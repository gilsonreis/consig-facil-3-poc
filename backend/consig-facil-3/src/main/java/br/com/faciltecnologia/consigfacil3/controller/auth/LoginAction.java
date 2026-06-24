package br.com.faciltecnologia.consigfacil3.controller.auth;

import br.com.faciltecnologia.consigfacil3.usecases.auth.LoginUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginInput;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LoginAction {

    private final LoginUseCase loginUseCase;

    @PostMapping("/login")
    public ResponseEntity<LoginOutput> execute(@RequestBody LoginInput input) {
        LoginOutput output = loginUseCase.execute(input);
        return ResponseEntity.ok(output);
    }
}
