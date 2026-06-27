package br.com.faciltecnologia.consigfacil3.controller.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.ContratoDetalhadoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.DetalharContratoUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/contratos")
public class DetalharContratoApiAction {

    private final DetalharContratoUseCase useCase;

    @GetMapping("/{id}")
    public ResponseEntity<ContratoDetalhadoOutput> execute(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.execute(id));
    }
}
