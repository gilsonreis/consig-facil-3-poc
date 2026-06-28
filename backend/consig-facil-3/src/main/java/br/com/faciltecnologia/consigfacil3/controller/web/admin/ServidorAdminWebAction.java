package br.com.faciltecnologia.consigfacil3.controller.web.admin;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.ListarServidoresUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorOutput;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
public class ServidorAdminWebAction {

    private final ListarServidoresUseCase listarServidoresUseCase;

    @GetMapping("/admin/servidores")
    public String execute(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestHeader(value = "HX-Request", required = false) boolean hxRequest,
            Model model) {

        ServidorFiltro filtro = new ServidorFiltro(search, null, null, null);
        Page<ServidorOutput> servidoresPage = listarServidoresUseCase.execute(filtro, PageRequest.of(page, size));

        model.addAttribute("servidoresPage", servidoresPage);
        model.addAttribute("search", search);
        model.addAttribute("currentSize", size);

        return hxRequest ? "admin/servidores/list :: tabela-fragment" : "admin/servidores/list";
    }
}
