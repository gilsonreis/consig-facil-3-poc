package br.com.faciltecnologia.consigfacil3.seeder;

import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.datafaker.Faker;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
@Slf4j
@org.springframework.context.annotation.Profile("!test")
public class CargaDadosSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final ServidorRepository servidorRepository;
    private final PasswordEncoder passwordEncoder;
    private final Faker faker = new Faker(new Locale("pt-BR"));

    @Override
    @Transactional
    public void run(String... args) {
        if (usuarioRepository.count() > 1) {
            log.info("Carga de dados de desenvolvimento já identificada. Abortando seeder para evitar duplicidade.");
            return;
        }

        log.info("Iniciando a geração de massa de dados realista (100 usuários)...");
        String senhaCriptografada = passwordEncoder.encode("Senha@123");

        for (int i = 0; i < 100; i++) {
            gerarUsuarioComVinculos(senhaCriptografada);
        }

        log.info("Massa de dados carregada com sucesso.");
    }

    private void gerarUsuarioComVinculos(String senha) {
        Usuario usuario = Usuario.builder()
                .nome(faker.name().fullName())
                .cpf(faker.cpf().valid().replaceAll("[^0-9]", ""))
                .email(faker.internet().emailAddress())
                .username(faker.internet().username())
                .senha(senha)
                .perfil(PerfilEnum.TOMADOR)
                .ativo(true)
                .build();

        usuarioRepository.save(usuario);

        int vinculosCount = ThreadLocalRandom.current().nextInt(1, 4);
        List<Servidor> servidores = new ArrayList<>();

        for (int i = 0; i < vinculosCount; i++) {
            servidores.add(criarVinculo(usuario));
        }

        servidorRepository.saveAll(servidores);
    }

    private Servidor criarVinculo(Usuario usuario) {
        BigDecimal margem = BigDecimal.valueOf(ThreadLocalRandom.current().nextDouble(500, 8001))
                .setScale(2, RoundingMode.HALF_UP);

        return Servidor.builder()
                .usuario(usuario)
                .matricula(faker.number().digits(8))
                .margemConsignavel(margem)
                .ativo(true)
                .build();
    }
}
