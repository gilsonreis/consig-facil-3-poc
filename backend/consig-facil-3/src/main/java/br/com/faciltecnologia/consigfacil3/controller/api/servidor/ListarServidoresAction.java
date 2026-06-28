package br.com.faciltecnologia.consigfacil3.controller.api.servidor;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.ListarServidoresUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/servidores")
@RequiredArgsConstructor
public class ListarServidoresAction {
    private final ListarServidoresUseCase listarServidoresUseCase;

    @GetMapping
    public ResponseEntity<Page<ServidorResumoOutput>> execute(
            ServidorFiltro filtro,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        return ResponseEntity.ok(listarServidoresUseCase.execute(filtro, pageable));
    }
}
