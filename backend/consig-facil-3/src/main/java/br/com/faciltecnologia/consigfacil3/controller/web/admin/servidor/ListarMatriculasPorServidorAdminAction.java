package br.com.faciltecnologia.consigfacil3.controller.web.admin.servidor;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ListarMatriculasPorServidorAdminAction {

    private final ServidorRepository servidorRepository;

    @GetMapping("/admin/servidores/{cpf}/matriculas")
    public String execute(@PathVariable String cpf, Model model) {
        List<Servidor> matriculas = servidorRepository.findByUsuarioCpf(cpf);
        model.addAttribute("matriculas", matriculas);
        model.addAttribute("cpf", cpf);

        if (!matriculas.isEmpty()) {
            model.addAttribute("nome", matriculas.get(0).getUsuario().getNome());
        }

        return "admin/servidores/fragmentos/modal-matriculas :: conteudo";
    }
}
