package br.com.faciltecnologia.consigfacil3.usecases.servidor.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.math.BigDecimal;

public record CadastrarServidorInput(
        @NotBlank(message = "O CPF é obrigatório")
        String cpf,

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

        @NotBlank(message = "A matrícula é obrigatória")
        String matricula,

        @NotNull(message = "A margem consignável é obrigatória")
        @PositiveOrZero(message = "A margem consignável deve ser zero ou positiva")
        BigDecimal margemConsignavel
) {}
