package br.com.faciltecnologia.consigfacil3.usecases.auth.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO de entrada para o fluxo de login utilizando Java Record para imutabilidade.
 */
public record LoginInput(String identificador, String senha) {

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("identificador", this.identificador);
        map.put("senha", this.senha);
        return map;
    }

    public static LoginInput fromMap(Map<String, Object> map) {
        return new LoginInput(
                (String) map.get("identificador"),
                (String) map.get("senha")
        );
    }
}
