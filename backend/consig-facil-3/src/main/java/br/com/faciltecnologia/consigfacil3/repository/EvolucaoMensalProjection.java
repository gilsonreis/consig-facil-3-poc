package br.com.faciltecnologia.consigfacil3.repository;

import java.math.BigDecimal;

public interface EvolucaoMensalProjection {
    Integer getMes();
    Integer getAno();
    BigDecimal getTotal();
}
