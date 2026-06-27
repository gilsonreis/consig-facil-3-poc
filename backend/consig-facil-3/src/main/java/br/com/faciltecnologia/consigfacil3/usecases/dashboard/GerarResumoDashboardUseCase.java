package br.com.faciltecnologia.consigfacil3.usecases.dashboard;

import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.DashboardResumoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.EvolucaoMensalOutput;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto.DashboardInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class GerarResumoDashboardUseCase {

    private final ServidorRepository servidorRepository;
    private final ContratoRepository contratoRepository;

    @Transactional(readOnly = true)
    public DashboardResumoOutput execute(DashboardInput input) {
        long totalServidoresAtivos = servidorRepository.countByAtivoTrue();
        
        long contratosAverbados = contratoRepository.countByStatus(StatusContrato.AVERBADO);
        
        BigDecimal volumeEmprestado = Optional.ofNullable(
                contratoRepository.sumValorTotalByStatus(StatusContrato.AVERBADO)
        ).orElse(BigDecimal.ZERO);

        LocalDateTime seisMesesAtras = LocalDateTime.now().minusMonths(6).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        
        List<EvolucaoMensalOutput> graficoEvolucao = contratoRepository.findEvolucaoMensal(StatusContrato.AVERBADO, seisMesesAtras)
                .stream()
                .map(projection -> new EvolucaoMensalOutput(
                        String.format("%02d/%d", projection.getMes(), projection.getAno()),
                        projection.getTotal()
                ))
                .collect(Collectors.toList());

        return new DashboardResumoOutput(
                totalServidoresAtivos,
                contratosAverbados,
                volumeEmprestado,
                graficoEvolucao
        );
    }
}
