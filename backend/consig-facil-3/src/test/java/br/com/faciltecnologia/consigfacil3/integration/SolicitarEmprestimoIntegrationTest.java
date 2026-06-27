package br.com.faciltecnologia.consigfacil3.integration.contrato;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.factories.ServidorFactory;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import br.com.faciltecnologia.consigfacil3.usecases.auth.dto.LoginInput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import tools.jackson.databind.ObjectMapper;
import org.hamcrest.Matchers;
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

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class SolicitarEmprestimoIntegrationTest {

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

    private String tokenAdmin;
    private Servidor servidorSalvo;

    @BeforeEach
    void setUp() throws Exception {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        historicoContratoRepository.deleteAll();
        contratoRepository.deleteAll();
        servidorRepository.deleteAll();
        usuarioRepository.deleteAll();

        // Criar usuário Admin para obter token
        Usuario admin = UsuarioFactory.criarEntidadeValida();
        admin.setId(null);
        admin.setPerfil(PerfilEnum.ADMIN);
        String senhaPura = admin.getSenha();
        admin.setSenha(passwordEncoder.encode(senhaPura));
        usuarioRepository.save(admin);

        // Criar Servidor para o empréstimo
        servidorSalvo = ServidorFactory.criarEntidadeValida(admin);
        servidorSalvo.setId(null);
        servidorSalvo.setAtivo(true);
        servidorSalvo.setMargemConsignavel(new BigDecimal("1000.00"));
        servidorRepository.save(servidorSalvo);

        // Obter Token
        LoginInput loginInput = new LoginInput(admin.getCpf(), senhaPura);
        String response = mockMvc.perform(post("/api/v1/auth/login").contextPath("/api/v1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginInput)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        
        tokenAdmin = objectMapper.readTree(response).get("token").asText();
    }

    @Test
    @DisplayName("Deve solicitar empréstimo com sucesso via API")
    void deveSolicitarEmprestimoComSucesso() throws Exception {
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorSalvo.getId(),
                new BigDecimal("5000.00"),
                new BigDecimal("2.0"),
                12
        );

        mockMvc.perform(post("/api/v1/contratos/solicitar").contextPath("/api/v1")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.contratoId").exists())
                .andExpect(jsonPath("$.status").value(StatusContrato.DIGITADO.name()))
                .andExpect(jsonPath("$.valorParcela").value(472.80)); // Tabela Price 5000, 2%, 12x
    }

    @Test
    @DisplayName("Deve retornar erro 422 ao tentar solicitar empréstimo com margem insuficiente")
    void deveRetornarErroQuandoMargemInsuficiente() throws Exception {
        SolicitarEmprestimoInput input = new SolicitarEmprestimoInput(
                servidorSalvo.getId(),
                new BigDecimal("50000.00"), // Valor alto para estourar margem
                new BigDecimal("2.0"),
                12
        );

        mockMvc.perform(post("/api/v1/contratos/solicitar").contextPath("/api/v1")
                        .header("Authorization", "Bearer " + tokenAdmin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.message").value(Matchers.containsString("Margem insuficiente")));
    }
}
