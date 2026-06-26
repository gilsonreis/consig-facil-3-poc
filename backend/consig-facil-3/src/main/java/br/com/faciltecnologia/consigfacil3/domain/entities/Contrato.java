package br.com.faciltecnologia.consigfacil3.domain.entities;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "contratos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Contrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servidor_id", nullable = false)
    private Servidor servidor;

    @Column(name = "valor_solicitado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorSolicitado;

    @Column(name = "taxa_juros_mes", nullable = false, precision = 5, scale = 2)
    private BigDecimal taxaJurosMes;

    @Column(name = "quantidade_parcelas", nullable = false)
    private Integer quantidadeParcelas;

    @Column(name = "valor_parcela", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorParcela;

    @Column(name = "valor_total_financiado", nullable = false, precision = 12, scale = 2)
    private BigDecimal valorTotalFinanciado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private StatusContrato status;

    @Column(name = "data_solicitacao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataSolicitacao = LocalDateTime.now();

    @OneToMany(mappedBy = "contrato", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Parcela> parcelas = new ArrayList<>();
}
