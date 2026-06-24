package br.com.faciltecnologia.consigfacil3.seeder;

import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class AdminSeeder implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    private static final String ADMIN_CPF = "00000000000";

    @Override
    public void run(String... args) {
        if (usuarioRepository.findByCpf(ADMIN_CPF).isEmpty()) {
            log.info("Criando usuário administrador inicial...");
            
            Usuario admin = Usuario.builder()
                    .cpf(ADMIN_CPF)
                    .nome("Administrador do Sistema")
                    .email("admin@consigfacil.com")
                    .senha(passwordEncoder.encode("admin123"))
                    .username("admin")
                    .perfil(PerfilEnum.ADMIN)
                    .ativo(true)
                    .build();

            usuarioRepository.save(admin);
            log.info("Usuário administrador criado com sucesso.");
        } else {
            log.info("Usuário administrador já existe. Nenhuma ação necessária.");
        }
    }
}
