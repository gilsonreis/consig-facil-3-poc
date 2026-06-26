package br.com.faciltecnologia.consigfacil3.factories;

import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import net.datafaker.Faker;

import java.util.Locale;

/**
 * Factory para geração de massa de dados de Usuários para testes.
 */
public class UsuarioFactory {

    private static final Faker faker = new Faker(new Locale("pt", "BR"));

    public static Usuario criarEntidadeValida() {
        return Usuario.builder()
                .id(faker.number().randomNumber())
                .nome(faker.name().fullName())
                .cpf(faker.cpf().valid().replaceAll("[^0-9]", ""))
                .email(faker.internet().emailAddress())
                .username(faker.internet().username())
                .senha("Senha@123") // Senha padrão para testes
                .perfil(PerfilEnum.TOMADOR)
                .ativo(true)
                .build();
    }

    public static Usuario criarEntidadeComCpf(String cpf) {
        return Usuario.builder()
                .id(faker.number().randomNumber())
                .nome(faker.name().fullName())
                .cpf(cpf.replaceAll("[^0-9]", ""))
                .email(faker.internet().emailAddress())
                .username(faker.internet().username())
                .senha("Senha@123")
                .perfil(PerfilEnum.TOMADOR)
                .ativo(true)
                .build();
    }
}
