package br.com.faciltecnologia.consigfacil3.usecases.servidor.dto;

public record ServidorResumoOutput(
        String nome,
        String cpf,
        Long quantidadeMatriculas
) {
}
