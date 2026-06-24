package br.com.faciltecnologia.consigfacil3.repository.spec;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

public class ServidorSpecification {

    public static Specification<Servidor> comFiltro(ServidorFiltro filtro) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Join com usuario para filtros e busca global
            Join<Servidor, Usuario> usuarioJoin = root.join("usuario", JoinType.INNER);

            // Busca Global
            if (StringUtils.hasText(filtro.termoBusca())) {
                String termo = "%" + filtro.termoBusca().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(usuarioJoin.get("nome")), termo),
                        cb.like(cb.lower(usuarioJoin.get("cpf")), termo),
                        cb.like(cb.lower(root.get("matricula")), termo)
                );
                predicates.add(searchPredicate);
            }

            // Filtro Ativo
            if (filtro.ativo() != null) {
                predicates.add(cb.equal(root.get("ativo"), filtro.ativo()));
            }

            // Margem Mínima
            if (filtro.margemMinima() != null) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("margemConsignavel"), filtro.margemMinima()));
            }

            // Margem Máxima
            if (filtro.margemMaxima() != null) {
                predicates.add(cb.lessThanOrEqualTo(root.get("margemConsignavel"), filtro.margemMaxima()));
            }

            // Resolver N+1: O JOIN FETCH deve ser feito apenas se a query não for de count
            if (query.getResultType().equals(Servidor.class)) {
                root.fetch("usuario", JoinType.INNER);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
