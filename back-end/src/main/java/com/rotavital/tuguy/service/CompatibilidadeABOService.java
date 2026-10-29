package com.rotavital.tuguy.service;

import org.springframework.stereotype.Service;

import java.util.Locale;
import java.util.Set;

@Service
public class CompatibilidadeABOService {

    public boolean testeABO(String tipoRequisicao, String tipoBolsa) {
        String receptor = normalizar(tipoRequisicao);
        String doador = normalizar(tipoBolsa);

        if (receptor == null || doador == null) {
            return false;
        }

        Set<String> tiposDoadorCompativeis = switch (receptor) {
            case "O-" -> Set.of("O-");
            case "O+" -> Set.of("O-", "O+");
            case "A-" -> Set.of("O-", "A-");
            case "A+" -> Set.of("O-", "O+", "A-", "A+");
            case "B-" -> Set.of("O-", "B-");
            case "B+" -> Set.of("O-", "O+", "B-", "B+");
            case "AB-" -> Set.of("O-", "A-", "B-", "AB-");
            case "AB+" -> Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");
            default -> Set.of();
        };

        return tiposDoadorCompativeis.contains(doador);
    }

    private String normalizar(String tipo) {
        if (tipo == null) {
            return null;
        }

        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        return normalizado.isEmpty() ? null : normalizado;
    }
}
