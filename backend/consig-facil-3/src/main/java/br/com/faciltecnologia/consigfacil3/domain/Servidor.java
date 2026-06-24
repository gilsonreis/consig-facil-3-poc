package br.com.faciltecnologia.consigfacil3.domain;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "servidores")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Servidor {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, unique = true, length = 50)
    private String matricula;

    @Column(name = "margem_consignavel", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal margemConsignavel = BigDecimal.ZERO;

    @Builder.Default
    private boolean ativo = true;

    @Column(name = "data_criacao", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime dataCriacao = LocalDateTime.now();
}
