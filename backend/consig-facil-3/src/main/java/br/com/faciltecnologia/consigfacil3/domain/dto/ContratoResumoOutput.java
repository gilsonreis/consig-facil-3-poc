package br.com.faciltecnologia.consigfacil3.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ContratoResumoOutput(
    Long id,
    BigDecimal valorTotal,
    BigDecimal valorParcela,
    Integer quantidadeParcelas,
    String status,
    LocalDateTime dataSolicitacao
) {}
