package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServidorRepository extends JpaRepository<Servidor, Long>, JpaSpecificationExecutor<Servidor> {
    boolean existsByMatricula(String matricula);
    List<Servidor> findByUsuarioId(Long usuarioId);
    long countByAtivoTrue();

    @Query("SELECT new br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput(u.nome, u.cpf, COUNT(s)) " +
           "FROM Servidor s JOIN s.usuario u " +
           "WHERE (:search IS NULL OR LOWER(u.nome) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(u.cpf) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%')) " +
           "OR LOWER(s.matricula) LIKE LOWER(CONCAT('%', CAST(:search AS string), '%'))) " +
           "GROUP BY u.cpf, u.nome")
    Page<ServidorResumoOutput> findResumoGroupedByCpf(@Param("search") String search, Pageable pageable);

    List<Servidor> findByUsuarioCpf(String cpf);
}
