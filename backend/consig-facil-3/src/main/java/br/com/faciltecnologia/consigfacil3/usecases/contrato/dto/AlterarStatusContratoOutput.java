package br.com.faciltecnologia.consigfacil3.usecases.contrato.dto;

import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;

public record AlterarStatusContratoOutput(Long contratoId, StatusContrato novoStatus) {}
