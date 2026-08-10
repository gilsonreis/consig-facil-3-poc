package br.com.faciltecnologia.consigfacil3.controller.api.servidor;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.ListarServidoresUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/servidores")
@RequiredArgsConstructor
public class ListarServidoresAction {
    private final ListarServidoresUseCase listarServidoresUseCase;
    private final ServidorRepository servidorRepository;

    @GetMapping
    public ResponseEntity<?> execute(
            ServidorFiltro filtro,
            @PageableDefault(page = 0, size = 10) Pageable pageable) {
        
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Usuario usuario) {
            if (usuario.getPerfil() == PerfilEnum.TOMADOR) {
                List<ServidorOutput> outputs = servidorRepository.findByUsuarioCpf(usuario.getCpf()).stream()
                        .map(ServidorOutput::fromEntity)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(outputs);
            }
        }
        
        return ResponseEntity.ok(listarServidoresUseCase.execute(filtro, pageable));
    }
}
