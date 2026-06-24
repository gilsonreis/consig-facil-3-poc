package br.com.faciltecnologia.consigfacil3.usecases.servidor.dto;

import java.math.BigDecimal;

public record ServidorFiltro(
        String termoBusca,
        Boolean ativo,
        BigDecimal margemMinima,
        BigDecimal margemMaxima
) {}
