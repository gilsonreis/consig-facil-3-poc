package br.com.faciltecnologia.consigfacil3.usecases.servidor.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CriarServidorInput(
        @NotNull(message = "O ID do usuário é obrigatório")
        Long usuarioId,
        @NotBlank(message = "A matrícula é obrigatória")
        String matricula,
        @NotNull(message = "A margem consignável é obrigatória")
        @PositiveOrZero(message = "A margem consignável deve ser zero ou positiva")
        BigDecimal margemConsignavel
) {}
