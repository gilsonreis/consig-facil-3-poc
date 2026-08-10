package br.com.faciltecnologia.consigfacil3.repository;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.factories.ServidorFactory;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import br.com.faciltecnologia.consigfacil3.repository.spec.ContratoSpecifications;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class ContratoRepositoryIT {

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private ServidorRepository servidorRepository;

    @Autowired
    private UsuarioRepository usuarioRepository;

    private Servidor s1;
    private Servidor s2;

    @BeforeEach
    void setupData() {
        contratoRepository.deleteAll();
        servidorRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Servidor 1: João Silva, CPF "12345678901", Matrícula "MAT-100"
        Usuario u1 = UsuarioFactory.criarEntidadeComCpf("12345678901");
        u1.setId(null);
        u1.setNome("João Silva");
        usuarioRepository.save(u1);

        s1 = ServidorFactory.criarEntidadeValida(u1);
        s1.setId(null);
        s1.setMatricula("MAT-100");
        servidorRepository.save(s1);

        // Servidor 2: Maria Souza, CPF "98765432100", Matrícula "MAT-200"
        Usuario u2 = UsuarioFactory.criarEntidadeComCpf("98765432100");
        u2.setId(null);
        u2.setNome("Maria Souza");
        usuarioRepository.save(u2);

        s2 = ServidorFactory.criarEntidadeValida(u2);
        s2.setId(null);
        s2.setMatricula("MAT-200");
        servidorRepository.save(s2);

        // Contrato 1: João Silva, R$ 5.000,00, Data: Ontem
        contratoRepository.save(Contrato.builder()
                .servidor(s1)
                .valorSolicitado(new BigDecimal("5000.00"))
                .taxaJurosMes(new BigDecimal("2.50"))
                .quantidadeParcelas(12)
                .valorParcela(new BigDecimal("450.00"))
                .valorTotalFinanciado(new BigDecimal("5400.00"))
                .status(StatusContrato.DIGITADO)
                .dataSolicitacao(LocalDateTime.now().minusDays(1))
                .build());

        // Contrato 2: Maria Souza, R$ 10.000,00, Data: Hoje
        contratoRepository.save(Contrato.builder()
                .servidor(s2)
                .valorSolicitado(new BigDecimal("10000.00"))
                .taxaJurosMes(new BigDecimal("2.00"))
                .quantidadeParcelas(24)
                .valorParcela(new BigDecimal("500.00"))
                .valorTotalFinanciado(new BigDecimal("12000.00"))
                .status(StatusContrato.AVERBADO)
                .dataSolicitacao(LocalDateTime.now())
                .build());

        // Contrato 3: João Silva, R$ 15.000,00, Data: 3 dias atrás
        contratoRepository.save(Contrato.builder()
                .servidor(s1)
                .valorSolicitado(new BigDecimal("15000.00"))
                .taxaJurosMes(new BigDecimal("1.80"))
                .quantidadeParcelas(36)
                .valorParcela(new BigDecimal("600.00"))
                .valorTotalFinanciado(new BigDecimal("21600.00"))
                .status(StatusContrato.AGUARDANDO_AVERBACAO)
                .dataSolicitacao(LocalDateTime.now().minusDays(3))
                .build());
    }

    @Test
    @DisplayName("Deve filtrar contratos por termo de busca de nome de servidor")
    void deveFiltrarPorNomeServidor() {
        Specification<Contrato> spec = ContratoSpecifications.porNomeOuCpfServidor("Maria");
        Page<Contrato> page = contratoRepository.findAll(spec, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getServidor().getUsuario().getNome()).isEqualTo("Maria Souza");
    }

    @Test
    @DisplayName("Deve filtrar contratos por CPF exato do servidor")
    void deveFiltrarPorCpfServidor() {
        Specification<Contrato> spec = ContratoSpecifications.porNomeOuCpfServidor("123.456.789-01");
        Page<Contrato> page = contratoRepository.findAll(spec, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(2); // João Silva tem 2 contratos
    }

    @Test
    @DisplayName("Deve filtrar contratos por matrícula do servidor")
    void deveFiltrarPorMatricula() {
        Specification<Contrato> spec = ContratoSpecifications.porMatricula("MAT-200");
        Page<Contrato> page = contratoRepository.findAll(spec, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(1);
        assertThat(page.getContent().get(0).getServidor().getMatricula()).isEqualTo("MAT-200");
    }

    @Test
    @DisplayName("Deve filtrar contratos por faixa de valor solicitado")
    void deveFiltrarPorRangeDeValor() {
        // Busca contratos entre R$ 4.000,00 e R$ 12.000,00 (Contrato 1 e 2)
        Specification<Contrato> spec = ContratoSpecifications.porRangeDeValor(new BigDecimal("4000.00"), new BigDecimal("12000.00"));
        Page<Contrato> page = contratoRepository.findAll(spec, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(2);
        assertThat(page.getContent()).extracting(Contrato::getValorSolicitado)
                .containsExactlyInAnyOrder(new BigDecimal("5000.00"), new BigDecimal("10000.00"));
    }

    @Test
    @DisplayName("Deve filtrar contratos por período de datas")
    void deveFiltrarPorPeriodo() {
        // Busca de ontem até hoje
        LocalDate hoje = LocalDate.now();
        LocalDate ontem = hoje.minusDays(1);

        Specification<Contrato> spec = ContratoSpecifications.porPeriodo(ontem, hoje);
        Page<Contrato> page = contratoRepository.findAll(spec, Pageable.unpaged());

        assertThat(page.getContent()).hasSize(2); // Contrato 1 e 2
    }
}
