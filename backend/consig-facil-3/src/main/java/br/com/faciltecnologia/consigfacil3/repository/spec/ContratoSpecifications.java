package br.com.faciltecnologia.consigfacil3.repository.spec;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ContratoSpecifications {

    public static Specification<Contrato> porNomeOuCpfServidor(String termo) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(termo)) {
                return null;
            }
            Join<Contrato, Servidor> servidorJoin = root.join("servidor", JoinType.INNER);
            Join<Servidor, Usuario> usuarioJoin = servidorJoin.join("usuario", JoinType.INNER);

            String termLike = "%" + termo.trim().toLowerCase() + "%";
            String cpfClean = termo.trim().replaceAll("\\D", "");

            if (cpfClean.length() == 11) {
                return cb.or(
                        cb.like(cb.lower(usuarioJoin.get("nome")), termLike),
                        cb.equal(usuarioJoin.get("cpf"), cpfClean)
                );
            } else {
                return cb.or(
                        cb.like(cb.lower(usuarioJoin.get("nome")), termLike),
                        cb.equal(usuarioJoin.get("cpf"), termo.trim())
                );
            }
        };
    }

    public static Specification<Contrato> porMatricula(String numeroMatricula) {
        return (root, query, cb) -> {
            if (!StringUtils.hasText(numeroMatricula)) {
                return null;
            }
            Join<Contrato, Servidor> servidorJoin = root.join("servidor", JoinType.INNER);
            return cb.equal(servidorJoin.get("matricula"), numeroMatricula.trim());
        };
    }

    public static Specification<Contrato> porPeriodo(LocalDate dataInicio, LocalDate dataFim) {
        return (root, query, cb) -> {
            if (dataInicio == null && dataFim == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (dataInicio != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("dataSolicitacao"), dataInicio.atStartOfDay()));
            }
            if (dataFim != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("dataSolicitacao"), dataFim.atTime(java.time.LocalTime.MAX)));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Contrato> porRangeDeValor(BigDecimal min, BigDecimal max) {
        return (root, query, cb) -> {
            if (min == null && max == null) {
                return null;
            }
            List<Predicate> predicates = new ArrayList<>();
            if (min != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("valorSolicitado"), min));
            }
            if (max != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("valorSolicitado"), max));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }

    public static Specification<Contrato> obterComServidorEUsuario() {
        return (root, query, cb) -> {
            if (query.getResultType().equals(Contrato.class)) {
                root.fetch("servidor", JoinType.INNER).fetch("usuario", JoinType.INNER);
            }
            return null;
        };
    }
}
