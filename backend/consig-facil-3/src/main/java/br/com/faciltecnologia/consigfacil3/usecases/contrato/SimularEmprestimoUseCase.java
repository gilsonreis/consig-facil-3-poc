package br.com.faciltecnologia.consigfacil3.usecases.contrato;

import br.com.faciltecnologia.consigfacil3.domain.dto.SimulacaoOutput;
import br.com.faciltecnologia.consigfacil3.usecases.contrato.dto.SolicitarEmprestimoInput;
import br.com.faciltecnologia.consigfacil3.exceptions.RegraNegocioException;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class SimularEmprestimoUseCase {

    public SimulacaoOutput execute(SolicitarEmprestimoInput input) {
        if (input.quantidadeParcelas() <= 0) {
            throw new RegraNegocioException("Quantidade de parcelas deve ser maior que zero.");
        }
        if (input.valorSolicitado().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RegraNegocioException("Valor solicitado deve ser maior que zero.");
        }

        BigDecimal valorParcela = calcularValorParcela(input.valorSolicitado(), input.taxaJurosMes(), input.quantidadeParcelas());
        BigDecimal valorTotal = valorParcela.multiply(BigDecimal.valueOf(input.quantidadeParcelas()));

        return new SimulacaoOutput(valorParcela, valorTotal);
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
}
