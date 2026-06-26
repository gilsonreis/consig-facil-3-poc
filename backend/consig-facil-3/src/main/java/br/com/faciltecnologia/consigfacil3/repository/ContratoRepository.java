package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long> {
}
