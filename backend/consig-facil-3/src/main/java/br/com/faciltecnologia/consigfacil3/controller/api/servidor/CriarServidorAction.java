package br.com.faciltecnologia.consigfacil3.controller.api.servidor;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.CriarServidorUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CriarServidorInput;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/servidores")
@RequiredArgsConstructor
public class CriarServidorAction {
    private final CriarServidorUseCase criarServidorUseCase;

    @PostMapping
    public ResponseEntity<ServidorOutput> execute(@RequestBody @Valid CriarServidorInput input) {
        return ResponseEntity.status(HttpStatus.CREATED).body(criarServidorUseCase.execute(input));
    }
}
