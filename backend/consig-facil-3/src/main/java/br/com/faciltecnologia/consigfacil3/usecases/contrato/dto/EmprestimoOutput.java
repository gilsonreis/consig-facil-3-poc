package br.com.faciltecnologia.consigfacil3.usecases.contrato.dto;

import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import java.math.BigDecimal;

public record EmprestimoOutput(
    Long contratoId,
    BigDecimal valorParcela,
    BigDecimal valorTotalFinanciado,
    StatusContrato status
) {}
