package br.com.faciltecnologia.consigfacil3.utils;

import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import net.datafaker.Faker;

import java.util.Locale;

public class UsuarioFactory {

    private static final Faker faker = new Faker(new Locale("pt-BR"));

    public static Usuario criarUsuarioValido() {
        return Usuario.builder()
                .nome(faker.name().fullName())
                .cpf(faker.cpf().valid().replaceAll("[^0-9]", ""))
                .email(faker.internet().emailAddress())
                .username(faker.internet().username())
                .senha("Senha@123") // Senha plana para o teste saber o que enviar
                .perfil(PerfilEnum.TOMADOR)
                .ativo(true)
                .build();
    }
}
