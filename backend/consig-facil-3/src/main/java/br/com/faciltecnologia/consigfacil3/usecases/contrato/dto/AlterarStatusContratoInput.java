package br.com.faciltecnologia.consigfacil3.usecases.contrato.dto;

import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import jakarta.validation.constraints.NotNull;

public record AlterarStatusContratoInput(
    @NotNull Long contratoId,
    @NotNull StatusContrato novoStatus,
    String observacao
) {}
