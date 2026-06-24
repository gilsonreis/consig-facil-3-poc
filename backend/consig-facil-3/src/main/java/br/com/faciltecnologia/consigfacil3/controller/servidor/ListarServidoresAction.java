package br.com.faciltecnologia.consigfacil3.controller.servidor;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.ListarServidoresUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/servidores")
@RequiredArgsConstructor
public class ListarServidoresAction {
    private final ListarServidoresUseCase listarServidoresUseCase;

    @GetMapping
    public ResponseEntity<List<ServidorOutput>> execute() {
        return ResponseEntity.ok(listarServidoresUseCase.execute());
    }
}
