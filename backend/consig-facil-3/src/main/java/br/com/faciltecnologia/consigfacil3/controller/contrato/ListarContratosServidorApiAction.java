package br.com.faciltecnologia.consigfacil3.controller.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.ContratoResumoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.ListarContratosServidorUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/servidores")
public class ListarContratosServidorApiAction {

    private final ListarContratosServidorUseCase useCase;

    @GetMapping("/{id}/contratos")
    public ResponseEntity<Page<ContratoResumoOutput>> execute(@PathVariable Long id, Pageable pageable) {
        return ResponseEntity.ok(useCase.execute(id, pageable));
    }
}
