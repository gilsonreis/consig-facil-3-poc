package br.com.faciltecnologia.consigfacil3.usecases.servidor;

import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorFiltro;
import br.com.faciltecnologia.consigfacil3.usecases.servidor.dto.ServidorResumoOutput;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListarServidoresUseCaseTest {

    @Mock
    private ServidorRepository servidorRepository;

    @InjectMocks
    private ListarServidoresUseCase useCase;

    @Test
    @DisplayName("Deve listar servidores agrupados por CPF com sucesso")
    void deveListarServidoresAgrupados() {
        // Arrange
        String termo = "João";
        ServidorFiltro filtro = new ServidorFiltro(termo, null, null, null);
        Pageable pageable = PageRequest.of(0, 10);
        
        List<ServidorResumoOutput> content = List.of(
                new ServidorResumoOutput("João Silva", "12345678901", 2L)
        );
        Page<ServidorResumoOutput> page = new PageImpl<>(content, pageable, 1);

        when(servidorRepository.findResumoGroupedByCpf(termo, pageable)).thenReturn(page);

        // Act
        Page<ServidorResumoOutput> result = useCase.execute(filtro, pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).nome()).isEqualTo("João Silva");
        assertThat(result.getContent().get(0).quantidadeMatriculas()).isEqualTo(2L);
    }
}
