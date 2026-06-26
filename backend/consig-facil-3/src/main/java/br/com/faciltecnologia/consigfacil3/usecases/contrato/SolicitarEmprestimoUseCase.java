package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.Servidor;
import br.com.faciltecnologia.consigfacil3.domain.entities.Contrato;
import br.com.faciltecnologia.consigfacil3.domain.entities.HistoricoContrato;
import br.com.faciltecnologia.consigfacil3.domain.entities.Parcela;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusContrato;
import br.com.faciltecnologia.consigfacil3.domain.enums.StatusParcela;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import br.com.faciltecnologia.consigfacil3.repository.ContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.HistoricoContratoRepository;
import br.com.faciltecnologia.consigfacil3.repository.ServidorRepository;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.EmprestimoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.ArrayList;

@Service
@RequiredArgsConstructor
public class SolicitarEmprestimoUseCase {

    private final ContratoRepository contratoRepository;
    private final HistoricoContratoRepository historicoContratoRepository;
    private final ServidorRepository servidorRepository;

    @Transactional
    public EmprestimoOutput execute(SolicitarEmprestimoInput input) {
        Servidor servidor = servidorRepository.findById(input.servidorId())
                .orElseThrow(() -> new RegraNegocioException("Servidor não encontrado."));

        if (!servidor.isAtivo()) {
            throw new RegraNegocioException("Servidor inativo.");
        }

        BigDecimal valorParcela = calcularValorParcela(input.valorSolicitado(), input.taxaJurosMes(), input.quantidadeParcelas());

        if (valorParcela.compareTo(servidor.getMargemConsignavel()) > 0) {
            throw new RegraNegocioException(String.format("Margem insuficiente. Parcela calculada: %s, Margem disponível: %s",
                    valorParcela, servidor.getMargemConsignavel()));
        }

        BigDecimal valorTotalFinanciado = valorParcela.multiply(BigDecimal.valueOf(input.quantidadeParcelas()));

        Contrato contrato = Contrato.builder()
                .servidor(servidor)
                .valorSolicitado(input.valorSolicitado())
                .taxaJurosMes(input.taxaJurosMes())
                .quantidadeParcelas(input.quantidadeParcelas())
                .valorParcela(valorParcela)
                .valorTotalFinanciado(valorTotalFinanciado)
                .status(StatusContrato.DIGITADO)
                .parcelas(new ArrayList<>())
                .build();

        gerarParcelas(contrato);

        Contrato contratoSalvo = contratoRepository.save(contrato);

        registrarHistorico(contratoSalvo);

        return new EmprestimoOutput(
                contratoSalvo.getId(),
                contratoSalvo.getValorParcela(),
                contratoSalvo.getValorTotalFinanciado(),
                contratoSalvo.getStatus()
        );
    }

    private BigDecimal calcularValorParcela(BigDecimal p, BigDecimal taxaMes, int n) {
        BigDecimal i = taxaMes.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP);
        
        // Formula: Parcela = P * (i * (1 + i)^n) / ((1 + i)^n - 1)
        BigDecimal umMaisI = BigDecimal.ONE.add(i);
        BigDecimal umMaisIPowN = umMaisI.pow(n);
        
        BigDecimal numerador = p.multiply(i).multiply(umMaisIPowN);
        BigDecimal denominador = umMaisIPowN.subtract(BigDecimal.ONE);
        
        return numerador.divide(denominador, 2, RoundingMode.HALF_UP);
    }

    private void gerarParcelas(Contrato contrato) {
        for (int numeroParcela = 1; numeroParcela <= contrato.getQuantidadeParcelas(); numeroParcela++) {
            Parcela parcela = Parcela.builder()
                    .contrato(contrato)
                    .numeroParcela(numeroParcela)
                    .valor(contrato.getValorParcela())
                    .dataVencimento(LocalDate.now().plusMonths(numeroParcela))
                    .status(StatusParcela.PENDENTE)
                    .build();
            contrato.getParcelas().add(parcela);
        }
    }

    private void registrarHistorico(Contrato contrato) {
        HistoricoContrato historico = HistoricoContrato.builder()
                .contrato(contrato)
                .statusNovo(StatusContrato.DIGITADO)
                .observacao("Contrato simulado e digitado pelo sistema")
                .build();
        historicoContratoRepository.save(historico);
    }
}
