package br.com.faciltecnologia.consigfacil3.usecases.auth.dto;

import java.util.HashMap;
import java.util.Map;

/**
 * DTO de saída para o fluxo de login utilizando Java Record.
 */
public record LoginOutput(String token, String tipo) {

    public LoginOutput(String token) {
        this(token, "Bearer");
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("token", this.token);
        map.put("tipo", this.tipo);
        return map;
    }

    public static LoginOutput fromMap(Map<String, Object> map) {
        return new LoginOutput(
                (String) map.get("token"),
                (String) map.get("tipo")
        );
    }
}
