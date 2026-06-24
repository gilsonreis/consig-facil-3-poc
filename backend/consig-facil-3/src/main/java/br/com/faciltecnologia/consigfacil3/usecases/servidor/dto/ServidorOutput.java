package br.com.faciltecnologia.consigfacil3.usecases.servidor.dto;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import java.math.BigDecimal;

public record ServidorOutput(
        Long id,
        String matricula,
        BigDecimal margemConsignavel,
        boolean ativo,
        String nomeUsuario,
        String cpfUsuario
) {
    public static ServidorOutput fromEntity(Servidor servidor) {
        return new ServidorOutput(
                servidor.getId(),
                servidor.getMatricula(),
                servidor.getMargemConsignavel(),
                servidor.isAtivo(),
                servidor.getUsuario().getNome(),
                servidor.getUsuario().getCpf()
        );
    }
}
