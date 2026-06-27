package br.com.faciltecnologia.consigfacil3.domain.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ParcelaOutput(
    Integer numero,
    BigDecimal valor,
    LocalDate vencimento,
    String status
) {}
