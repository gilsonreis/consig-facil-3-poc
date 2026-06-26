package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.domain.entities.HistoricoContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricoContratoRepository extends JpaRepository<HistoricoContrato, Long> {
}
