package br.com.faciltecnologia.consigfacil3.controller.web.admin.servidor;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CadastrarServidorInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class NovoServidorFormAdminAction {

    @GetMapping("/admin/servidores/novo")
    public String execute(Model model) {
        if (!model.containsAttribute("cadastrarServidorInput")) {
            model.addAttribute("cadastrarServidorInput", new CadastrarServidorInput("", "", "", "", null));
        }
        return "admin/servidores/form";
    }
}
