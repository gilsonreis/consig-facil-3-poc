package br.com.faciltecnologia.consigfacil3.controller.web.admin.servidor;

import br.com.faciltecnologia.consigfacil3.usecases.servidor.CadastrarServidorUseCase;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CadastrarServidorInput;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequiredArgsConstructor
public class CadastrarServidorAdminAction {

    private final CadastrarServidorUseCase cadastrarServidorUseCase;

    @PostMapping("/admin/servidores/novo")
    public String execute(@Valid CadastrarServidorInput input, BindingResult result, RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.cadastrarServidorInput", result);
            redirectAttributes.addFlashAttribute("cadastrarServidorInput", input);
            return "redirect:/admin/servidores/novo";
        }

        try {
            cadastrarServidorUseCase.execute(input);
            redirectAttributes.addFlashAttribute("successMessage", "Servidor cadastrado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            redirectAttributes.addFlashAttribute("cadastrarServidorInput", input);
            return "redirect:/admin/servidores/novo";
        }

        return "redirect:/admin/servidores";
    }
}
