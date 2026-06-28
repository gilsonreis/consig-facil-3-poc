package br.com.faciltecnologia.consigfacil3.controller.web.admin;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.ListarServidoresUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ServidorAdminWebAction {

    private final ListarServidoresUseCase listarServidoresUseCase;
    private final ServidorRepository servidorRepository;

    @GetMapping("/admin/servidores")
    public String execute(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "u.nome,asc") String sort,
            @RequestHeader(value = "HX-Request", required = false) boolean hxRequest,
            Model model) {

        String[] sortParts = sort.split(",");
        String sortField = sortParts[0];
        String sortDir = sortParts.length > 1 ? sortParts[1] : "asc";

        Sort sortObj = Sort.by(sortDir.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC, sortField);
        Pageable pageable = PageRequest.of(page, size, sortObj);

        ServidorFiltro filtro = new ServidorFiltro(search, null, null, null);
        Page<ServidorResumoOutput> servidoresPage = listarServidoresUseCase.execute(filtro, pageable);

        model.addAttribute("servidoresPage", servidoresPage);
        model.addAttribute("search", search);
        model.addAttribute("currentSize", size);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);

        return hxRequest ? "admin/servidores/list :: tabela-fragment" : "admin/servidores/list";
    }

    @GetMapping("/admin/servidores/{cpf}/matriculas")
    public String listarMatriculas(@PathVariable String cpf, Model model) {
        List<Servidor> matriculas = servidorRepository.findByUsuarioCpf(cpf);
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("cpf", cpf);
        
        if (!matriculas.isEmpty()) {
            model.addAttribute("nome", matriculas.get(0).getUsuario().getNome());
        }

        return "admin/servidores/fragmentos/modal-matriculas :: conteudo";
    }
}
