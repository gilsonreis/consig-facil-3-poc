package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.factories.ServidorFactory;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import br.com.faciltecnologia.consigfacil3.repository.spec.ServidorSpecification;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class ServidorRepositoryIT {

    @Autowired
    private ServidorRepository servidorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @BeforeEach
    void setupMassaDeDados() {
        servidorRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Servidor 1: Usuário "João da Silva", CPF "11111111111", Matrícula "MAT-100", Margem 1000.00, Ativo = true.
        Usuario joao = UsuarioFactory.criarEntidadeComCpf("11111111111");
        joao.setId(null);
        joao.setNome("João da Silva");
        usuarioRepository.save(joao);
        
        Servidor s1 = ServidorFactory.criarEntidadeValida(joao);
        s1.setId(null);
        s1.setMatricula("MAT-100");
        s1.setMargemConsignavel(new BigDecimal("1000.00"));
        s1.setAtivo(true);
        servidorRepository.save(s1);

        // Servidor 2: Usuário "Maria Souza", CPF "22222222222", Matrícula "MAT-200", Margem 5000.00, Ativo = true.
        Usuario maria = UsuarioFactory.criarEntidadeComCpf("22222222222");
        maria.setId(null);
        maria.setNome("Maria Souza");
        usuarioRepository.save(maria);

        Servidor s2 = ServidorFactory.criarEntidadeValida(maria);
        s2.setId(null);
        s2.setMatricula("MAT-200");
        s2.setMargemConsignavel(new BigDecimal("5000.00"));
        s2.setAtivo(true);
        servidorRepository.save(s2);

        // Servidor 3: Usuário "Carlos Silva", CPF "33333333333", Matrícula "MAT-300", Margem 8000.00, Ativo = false.
        Usuario carlos = UsuarioFactory.criarEntidadeComCpf("33333333333");
        carlos.setId(null);
        carlos.setNome("Carlos Silva");
        usuarioRepository.save(carlos);

        Servidor s3 = ServidorFactory.criarEntidadeValida(carlos);
        s3.setId(null);
        s3.setMatricula("MAT-300");
        s3.setMargemConsignavel(new BigDecimal("8000.00"));
        s3.setAtivo(false);
        servidorRepository.save(s3);

        // Servidor 4: (Aleatório usando apenas os defaults da Factory para gerar volume).
        Usuario aleatorio = UsuarioFactory.criarEntidadeValida();
        aleatorio.setId(null);
        usuarioRepository.save(aleatorio);
        
        Servidor s4 = ServidorFactory.criarEntidadeValida(aleatorio);
        s4.setId(null);
        servidorRepository.save(s4);
    }

    @Test
    @DisplayName("Cenário 1: Teste de Paginação Pura")
    void devePaginarCorretamente() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 2);
        ServidorFiltro filtro = new ServidorFiltro(null, null, null, null);

        // Act
        Page<Servidor> page = servidorRepository.findAll(ServidorSpecification.comFiltro(filtro), pageable);

        // Assert
        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getTotalElements()).isEqualTo(4);
    }

    @Test
    @DisplayName("Cenário 2: Teste de Busca Global (Termo Busca - OR)")
    void deveFiltrarPorTermoBusca() {
        // Arrange
        ServidorFiltro filtroSilva = new ServidorFiltro("silva", null, null, null);
        ServidorFiltro filtroCpf = new ServidorFiltro("2222", null, null, null);
        ServidorFiltro filtroMatricula = new ServidorFiltro("MAT-100", null, null, null);

        // Act
        Page<Servidor> pageSilva = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroSilva), Pageable.unpaged());
        Page<Servidor> pageCpf = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroCpf), Pageable.unpaged());
        Page<Servidor> pageMatricula = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroMatricula), Pageable.unpaged());

        // Assert
        assertThat(pageSilva.getContent())
                .extracting(s -> s.getUsuario().getNome())
                .containsExactlyInAnyOrder("João da Silva", "Carlos Silva");

        assertThat(pageCpf.getContent())
                .extracting(s -> s.getUsuario().getNome())
                .containsExactlyInAnyOrder("Maria Souza");

        assertThat(pageMatricula.getContent())
                .extracting(Servidor::getMatricula)
                .containsExactlyInAnyOrder("MAT-100");
    }

    @Test
    @DisplayName("Cenário 3: Teste de Filtro Booleano (Ativo/Inativo)")
    void deveFiltrarPorStatusAtivo() {
        // Arrange
        ServidorFiltro filtroInativo = new ServidorFiltro(null, false, null, null);

        // Act
        Page<Servidor> page = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroInativo), Pageable.unpaged());

        // Assert
        assertThat(page.getContent())
                .extracting(s -> s.getUsuario().getNome())
                .containsExactlyInAnyOrder("Carlos Silva");
    }

    @Test
    @DisplayName("Cenário 4: Teste de Faixa de Margem (Between)")
    void deveFiltrarPorFaixaDeMargem() {
        // Arrange
        // Servidor 1: 1000.00
        // Servidor 2: 5000.00 (Maria Souza)
        // Servidor 3: 8000.00 (Carlos Silva)
        // Servidor 4: Aleatório (pode ter qualquer valor entre 1000 e 10000)

        ServidorFiltro filtroFaixa = new ServidorFiltro(null, null, new BigDecimal("2000.00"), new BigDecimal("6000.00"));
        ServidorFiltro filtroMin = new ServidorFiltro(null, null, new BigDecimal("8000.00"), null);

        // Act
        Page<Servidor> pageFaixa = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroFaixa), Pageable.unpaged());
        Page<Servidor> pageMin = servidorRepository.findAll(ServidorSpecification.comFiltro(filtroMin), Pageable.unpaged());

        // Assert
        assertThat(pageFaixa.getContent())
                .extracting(s -> s.getUsuario().getNome())
                .contains("Maria Souza");

        assertThat(pageMin.getContent())
                .extracting(s -> s.getUsuario().getNome())
                .contains("Carlos Silva");
    }
}
