package br.com.faciltecnologia.consigfacil3.usecases.contrato.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

public record SolicitarEmprestimoInput(
    @NotNull
    Long servidorId,
    
    @NotNull
    @Positive
    BigDecimal valorSolicitado,
    
    @NotNull
    @Positive
    BigDecimal taxaJurosMes,
    
    @NotNull
    @Min(1)
    Integer quantidadeParcelas
) {}
