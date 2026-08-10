package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContratoRepository extends JpaRepository<Contrato, Long>, JpaSpecificationExecutor<Contrato> {
    long countByStatus(StatusContrato status);
    org.springframework.data.domain.Page<Contrato> findByServidorId(Long servidorId, org.springframework.data.domain.Pageable pageable);
    List<Contrato> findByServidorId(Long servidorId);

    @Query("SELECT SUM(c.valorTotalFinanciado) FROM Contrato c WHERE c.status = :status")
    BigDecimal sumValorTotalByStatus(@Param("status") StatusContrato status);

    @Query("""
        SELECT
            EXTRACT(MONTH FROM c.dataSolicitacao) as mes,
            EXTRACT(YEAR FROM c.dataSolicitacao) as ano,
            SUM(c.valorTotalFinanciado) as total
        FROM Contrato c
        WHERE c.status = :status
          AND c.dataSolicitacao >= :dataInicial
        GROUP BY ano, mes
        ORDER BY ano DESC, mes DESC
    """)
    List<EvolucaoMensalProjection> findEvolucaoMensal(
            @Param("status") StatusContrato status,
            @Param("dataInicial") LocalDateTime dataInicial
    );
}
