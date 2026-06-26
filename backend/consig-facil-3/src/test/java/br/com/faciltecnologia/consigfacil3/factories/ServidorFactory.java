package br.com.faciltecnologia.consigfacil3.factories;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.CriarServidorInput;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.util.Locale;

/**
 * Factory para geração de massa de dados de Servidores para testes.
 */
public class ServidorFactory {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    public static Servidor criarEntidadeValida(Usuario usuario) {
        return Servidor.builder()
                .id(faker.number().randomNumber())
                .usuario(usuario)
                .matricula(faker.number().digits(8))
                .margemConsignavel(BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000)))
                .ativo(true)
                .build();
    }

    public static CriarServidorInput criarInputValido(Long usuarioId) {
        return new CriarServidorInput(
                usuarioId,
                faker.number().digits(8),
                BigDecimal.valueOf(faker.number().randomDouble(2, 1000, 10000))
        );
    }

    public static CriarServidorInput criarInputComMargemNegativa(Long usuarioId) {
        return new CriarServidorInput(
                usuarioId,
                faker.number().digits(8),
                BigDecimal.valueOf(faker.number().randomDouble(2, 100, 1000)).negate()
        );
    }
}
