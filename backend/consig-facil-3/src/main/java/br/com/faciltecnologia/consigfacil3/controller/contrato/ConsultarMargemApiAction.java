package br.com.faciltecnologia.consigfacil3.controller.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.MargemOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.ConsultarMargemUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/servidores")
public class ConsultarMargemApiAction {

    private final ConsultarMargemUseCase useCase;

    @GetMapping("/{id}/margem")
    public ResponseEntity<MargemOutput> execute(@PathVariable Long id) {
        return ResponseEntity.ok(useCase.execute(id));
    }
}
