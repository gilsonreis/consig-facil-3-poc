package br.com.faciltecnologia.consigfacil3.controller.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.SimulacaoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.SimularEmprestimoUseCase;
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
public class SimularEmprestimoApiAction {

    private final SimularEmprestimoUseCase useCase;

    @PostMapping("/simular")
    public ResponseEntity<SimulacaoOutput> execute(@Valid @RequestBody SolicitarEmprestimoInput input) {
        return ResponseEntity.ok(useCase.execute(input));
    }
}
