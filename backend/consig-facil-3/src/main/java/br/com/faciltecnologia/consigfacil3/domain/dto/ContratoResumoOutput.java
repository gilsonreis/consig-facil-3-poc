package br.com.faciltecnologia.consigfacil3.domain.dto;

import java.math.BigDecimal;

public record ContratoResumoOutput(
    Long id,
    BigDecimal valorTotal,
    Integer quantidadeParcelas,
    String status
) {}
