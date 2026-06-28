package br.com.faciltecnologia.consigfacil3.controller.web.admin.servidor;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CadastrarServidorInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class BuscarUsuarioCpfAdminAction {

    private final UsuarioRepository usuarioRepository;

    @GetMapping("/admin/usuarios/busca-cpf")
    public String execute(@RequestParam String cpf, Model model) {
        Optional<Usuario> usuarioOpt = usuarioRepository.findByCpf(cpf);
        
        if (usuarioOpt.isPresent()) {
            model.addAttribute("usuario", usuarioOpt.get());
            model.addAttribute("usuarioExistente", true);
        } else {
            model.addAttribute("usuarioExistente", false);
        }

        // Adiciona um DTO vazio para evitar erros de BindingResult no fragmento
        model.addAttribute("cadastrarServidorInput", new CadastrarServidorInput(cpf, "", "", "", null));

        return "admin/servidores/fragmentos/form-dados-pessoais :: conteudo";
    }
}
