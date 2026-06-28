package br.com.faciltecnologia.consigfacil3.integration;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.formLogin;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders.logout;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class AdminAuthIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServidorRepository servidorRepository;

    @Autowired
    private ContratoRepository contratoRepository;

    @Autowired
    private HistoricoContratoRepository historicoContratoRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();
        
        historicoContratoRepository.deleteAll();
        contratoRepository.deleteAll();
        servidorRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve realizar login administrativo com sucesso e redirecionar para o dashboard")
    void deveRealizarLoginComSucesso() throws Exception {
        Usuario admin = UsuarioFactory.criarEntidadeValida();
        admin.setId(null);
        admin.setPerfil(PerfilEnum.ADMIN);
        String senhaPura = admin.getSenha();
        admin.setSenha(passwordEncoder.encode(senhaPura));
        usuarioRepository.save(admin);

        mockMvc.perform(formLogin("/admin/auth/login")
                        .user(admin.getUsername())
                        .password(senhaPura))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/dashboard"));
    }

    @Test
    @DisplayName("Deve falhar login administrativo com senha errada e redirecionar com erro")
    void deveFalharLoginComSenhaErrada() throws Exception {
        Usuario admin = UsuarioFactory.criarEntidadeValida();
        admin.setId(null);
        admin.setPerfil(PerfilEnum.ADMIN);
        admin.setSenha(passwordEncoder.encode(admin.getSenha()));
        usuarioRepository.save(admin);

        mockMvc.perform(formLogin("/admin/auth/login")
                        .user(admin.getUsername())
                        .password("senha_errada"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/auth/login?error=true"));
    }

    @Test
    @DisplayName("Deve barrar acesso ao dashboard sem estar autenticado")
    void deveBarrarAcessoDashboardSemAutenticacao() throws Exception {
        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/auth/login"));
    }

    @Test
    @DisplayName("Deve realizar logout e redirecionar para a página de login com sucesso")
    void deveRealizarLogoutComSucesso() throws Exception {
        mockMvc.perform(get("/admin/auth/logout"))
                .andExpect(status().isFound())
                .andExpect(redirectedUrl("/admin/auth/login?logout=true"));
    }
}
