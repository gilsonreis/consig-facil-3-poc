package br.com.faciltecnologia.consigfacil3.controller.api.contrato;

import br.com.faciltecnologia.consigfacil3.usecases.contrato.SolicitarEmprestimoUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.EmprestimoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contratos")
public class SolicitarEmprestimoApiAction {

    private final SolicitarEmprestimoUseCase useCase;

    @PostMapping("/solicitar")
    public ResponseEntity<EmprestimoOutput> execute(@Valid @RequestBody SolicitarEmprestimoInput input) {
        return ResponseEntity.ok(useCase.execute(input));
    }
}
