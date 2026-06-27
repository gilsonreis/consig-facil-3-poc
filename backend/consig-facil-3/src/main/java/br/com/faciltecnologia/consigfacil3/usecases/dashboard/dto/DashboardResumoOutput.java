package br.com.faciltecnologia.consigfacil3.usecases.dashboard.dto;

import java.math.BigDecimal;
import java.util.List;

public record DashboardResumoOutput(
        Long totalServidoresAtivos,
        Long contratosAverbados,
        BigDecimal volumeEmprestado,
        List<EvolucaoMensalOutput> graficoEvolucao
) {}
