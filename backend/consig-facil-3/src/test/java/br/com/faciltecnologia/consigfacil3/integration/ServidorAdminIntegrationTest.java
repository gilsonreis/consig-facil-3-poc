package br.com.faciltecnologia.consigfacil3.integration;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.Usuario;
import br.com.faciltecnologia.consigfacil3.factories.UsuarioFactory;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.repository.UsuarioRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class ServidorAdminIntegrationTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private ServidorRepository servidorRepository;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext)
                .apply(springSecurity())
                .build();

        servidorRepository.deleteAll();
        usuarioRepository.deleteAll();
    }

    @Test
    @DisplayName("Deve listar servidores com ordenação padrão por nome ASC")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarServidoresComOrdenacaoPadrao() throws Exception {
        criarServidor("Zelia", "11111111111");
        criarServidor("Ana", "22222222222");

        mockMvc.perform(get("/admin/servidores"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortField", "u.nome"))
                .andExpect(model().attribute("sortDir", "asc"))
                .andExpect(view().name("admin/servidores/list"));
    }

    @Test
    @DisplayName("Deve listar servidores com ordenação por CPF DESC")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveListarServidoresComOrdenacaoPorCpfDesc() throws Exception {
        criarServidor("Zelia", "11111111111");
        criarServidor("Ana", "22222222222");

        mockMvc.perform(get("/admin/servidores")
                        .param("sort", "u.cpf,desc"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("sortField", "u.cpf"))
                .andExpect(model().attribute("sortDir", "desc"));
    }

    private void criarServidor(String nome, String cpf) {
        Usuario usuario = UsuarioFactory.criarEntidadeValida();
        usuario.setId(null);
        usuario.setNome(nome);
        usuario.setCpf(cpf);
        usuario.setUsername(cpf);
        usuario.setPerfil(PerfilEnum.TOMADOR);
        usuarioRepository.save(usuario);

        Servidor servidor = new Servidor();
        servidor.setUsuario(usuario);
        servidor.setMatricula("MAT-" + cpf);
        servidor.setMargemConsignavel(new BigDecimal("1000.00"));
        servidor.setAtivo(true);
        servidorRepository.save(servidor);
    }
}
