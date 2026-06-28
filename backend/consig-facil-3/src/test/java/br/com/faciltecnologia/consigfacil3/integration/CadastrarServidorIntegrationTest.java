package br.com.faciltecnologia.consigfacil3.integration;

import br.com.faciltecnologia.consigfacil3.TestcontainersConfiguration;
import br.com.faciltecnologia.consigfacil3.domain.PerfilEnum;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@org.springframework.test.context.ActiveProfiles("test")
class CadastrarServidorIntegrationTest {

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
    @DisplayName("Deve retornar fragmento de dados pessoais quando buscar CPF existente")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarFragmentoQuandoBuscarCpfExistente() throws Exception {
        Usuario usuario = UsuarioFactory.criarEntidadeValida();
        usuario.setId(null);
        usuario.setCpf("12345678901");
        usuarioRepository.save(usuario);

        mockMvc.perform(get("/admin/usuarios/busca-cpf").param("cpf", "12345678901"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/servidores/fragmentos/form-dados-pessoais :: conteudo"))
                .andExpect(model().attribute("usuarioExistente", true))
                .andExpect(model().attributeExists("usuario"));
    }

    @Test
    @DisplayName("Deve retornar flag usuarioExistente false quando buscar CPF inexistente")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveRetornarFlagFalseQuandoBuscarCpfInexistente() throws Exception {
        mockMvc.perform(get("/admin/usuarios/busca-cpf").param("cpf", "00000000000"))
                .andExpect(status().isOk())
                .andExpect(model().attribute("usuarioExistente", false));
    }

    @Test
    @DisplayName("Deve cadastrar novo servidor e criar novo usuário quando CPF não existe")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCadastrarNovoServidorENovoUsuario() throws Exception {
        mockMvc.perform(post("/admin/servidores/novo")
                        .param("cpf", "12345678901")
                        .param("nome", "Novo Servidor")
                        .param("email", "novo@email.com")
                        .param("matricula", "12345")
                        .param("margemConsignavel", "1000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/servidores"));

        assertTrue(usuarioRepository.findByCpf("12345678901").isPresent());
        assertTrue(servidorRepository.existsByMatricula("12345"));
        
        Usuario usuario = usuarioRepository.findByCpf("12345678901").get();
        assertEquals(PerfilEnum.TOMADOR, usuario.getPerfil());
    }

    @Test
    @DisplayName("Deve cadastrar novo servidor vinculado a usuário existente")
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    void deveCadastrarNovoServidorVinculadoAUsuarioExistente() throws Exception {
        Usuario usuario = UsuarioFactory.criarEntidadeValida();
        usuario.setId(null);
        usuario.setCpf("12345678901");
        usuario.setNome("Usuario Ja Existe");
        usuarioRepository.save(usuario);

        mockMvc.perform(post("/admin/servidores/novo")
                        .param("cpf", "12345678901")
                        .param("nome", "Qualquer Nome") // Deve ser ignorado pelo UseCase se usar o do banco
                        .param("email", "outro@email.com")
                        .param("matricula", "54321")
                        .param("margemConsignavel", "2000.00"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/servidores"));

        assertEquals(1, usuarioRepository.count());
        assertTrue(servidorRepository.existsByMatricula("54321"));
    }
}
