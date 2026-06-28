package br.com.faciltecnologia.consigfacil3.integration;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginInput;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import tools.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class AuthIntegrationTest {

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

    @Autowired
    private ObjectMapper objectMapper;

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
    @DisplayName("Cenário 1: Deve barrar acesso a rota protegida sem token")
    void deveRetornarForbiddenSemToken() throws Exception {
        mockMvc.perform(get("/api/v1/hello"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Cenário 2: Deve barrar acesso com token inválido")
    void deveRetornarForbiddenComTokenInvalido() throws Exception {
        mockMvc.perform(get("/api/v1/hello")
                        .header("Authorization", "Bearer token_inventado"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("Cenário 3 e 4: Login com sucesso e acesso a rota protegida (CPF, Email e Username)")
    void deveRealizarLoginEAAcessarRotaProtegidaComDiferentesIdentificadores() throws Exception {
        // Preparação: Salvar usuário com senha encriptada
        Usuario usuario = UsuarioFactory.criarEntidadeValida();
        usuario.setId(null);
        String senhaPura = usuario.getSenha();
        usuario.setSenha(passwordEncoder.encode(senhaPura));
        usuarioRepository.save(usuario);

        // Teste com CPF
        testarLoginESucesso(usuario.getCpf(), senhaPura);
        
        // Teste com Email
        testarLoginESucesso(usuario.getEmail(), senhaPura);
        
        // Teste com Username
        testarLoginESucesso(usuario.getUsername(), senhaPura);
    }

    private void testarLoginESucesso(String identificador, String senha) throws Exception {
        LoginInput loginInput = new LoginInput(identificador, senha);

        String response = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInput)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists())
                .andReturn().getResponse().getContentAsString();

        String token = objectMapper.readTree(response).get("token").asText();

        // Acesso à rota protegida
        mockMvc.perform(get("/api/v1/hello")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Cenário 5: Deve falhar login com senha errada ou usuário inexistente")
    void deveRetornarUnauthorizedParaFalhasDeLogin() throws Exception {
        // 1. Senha errada
        Usuario usuario = UsuarioFactory.criarEntidadeValida();
        usuario.setId(null);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        usuarioRepository.save(usuario);

        LoginInput loginSenhaErrada = new LoginInput(usuario.getCpf(), "senha_incorreta");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginSenhaErrada)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401))
                .andExpect(jsonPath("$.error").exists())
                .andExpect(jsonPath("$.message").exists());

        // 2. Identificador inexistente
        LoginInput loginInexistente = new LoginInput("99999999999", "senha");

        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInexistente)))
                .andExpect(status().isUnauthorized());
    }
}
