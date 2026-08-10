package br.com.faciltecnologia.consigfacil3.usecases.auth.dto;

public record UsuarioPerfilOutput(
        Long id,
        String nome,
        String cpf,
        String email
) {}
