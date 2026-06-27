package br.com.faciltecnologia.consigfacil3.domain.dto;

import java.time.LocalDateTime;

public record HistoricoOutput(
    LocalDateTime data,
    String status,
    String observacao
) {}
