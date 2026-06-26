package br.com.faciltecnologia.consigfacil3.domain.entities;

import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_contratos")
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class HistoricoContrato {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_id", nullable = false)
    private Contrato contrato;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_anterior", length = 50)
    private StatusContrato statusAnterior;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_novo", nullable = false, length = 50)
    private StatusContrato statusNovo;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(name = "data_evento", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataEvento = LocalDateTime.now();
}
