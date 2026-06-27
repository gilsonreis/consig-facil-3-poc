package br.com.faciltecnologia.consigfacil3.domain.dto;

import java.math.BigDecimal;
import java.util.List;

public record ContratoDetalhadoOutput(
    Long id,
    BigDecimal valorTotal,
    Integer quantidadeParcelas,
    String status,
    List<ParcelaOutput> parcelas,
    List<HistoricoOutput> historico
) {}
