package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ServidorRepository extends JpaRepository<Servidor, Long> {
    boolean existsByMatricula(String matricula);
    List<Servidor> findByUsuarioId(Long usuarioId);
}
