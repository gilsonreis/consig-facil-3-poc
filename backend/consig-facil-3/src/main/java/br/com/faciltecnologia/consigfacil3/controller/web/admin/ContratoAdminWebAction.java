package br.com.faciltecnologia.consigfacil3.controller.web.admin;

import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.spec.ContratoSpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.math.BigDecimal;
import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
public class ContratoAdminWebAction {

    private final ContratoRepository contratoRepository;

    @GetMapping("/admin/contratos")
    public String execute(
            @RequestParam(required = false) String servidor,
            @RequestParam(required = false) String matricula,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dataFim,
            @RequestParam(required = false) BigDecimal valorMin,
            @RequestParam(required = false) BigDecimal valorMax,
            @PageableDefault(size = 10, sort = "dataSolicitacao", direction = Sort.Direction.DESC) Pageable pageable,
            @RequestHeader(value = "HX-Request", required = false) boolean hxRequest,
            Model model) {

        Specification<Contrato> spec = Specification.where(ContratoSpecifications.obterComServidorEUsuario());

        if (servidor != null && !servidor.isBlank()) {
            spec = spec.and(ContratoSpecifications.porNomeOuCpfServidor(servidor));
        }
        if (matricula != null && !matricula.isBlank()) {
            spec = spec.and(ContratoSpecifications.porMatricula(matricula));
        }
        if (dataInicio != null || dataFim != null) {
            spec = spec.and(ContratoSpecifications.porPeriodo(dataInicio, dataFim));
        }
        if (valorMin != null || valorMax != null) {
            spec = spec.and(ContratoSpecifications.porRangeDeValor(valorMin, valorMax));
        }

        Page<Contrato> contratosPage = contratoRepository.findAll(spec, pageable);

        String sortField = "dataSolicitacao";
        String sortDir = "desc";
        if (pageable.getSort().isSorted()) {
            Sort.Order order = pageable.getSort().iterator().next();
            sortField = order.getProperty();
            sortDir = order.getDirection().name().toLowerCase();
        }

        model.addAttribute("contratosPage", contratosPage);
        model.addAttribute("servidor", servidor);
        model.addAttribute("matricula", matricula);
        model.addAttribute("dataInicio", dataInicio);
        model.addAttribute("dataFim", dataFim);
        model.addAttribute("valorMin", valorMin);
        model.addAttribute("valorMax", valorMax);
        model.addAttribute("sortField", sortField);
        model.addAttribute("sortDir", sortDir);
        model.addAttribute("currentSize", pageable.getPageSize());

        return hxRequest ? "admin/contratos/list :: conteudo" : "admin/contratos/list";
    }
}
