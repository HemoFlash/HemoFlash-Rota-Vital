package com.rotavital.tuguy.service;

import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
public class CompatibilidadeABOService {

    private static final String[] TIPOS_SANGUINEOS = {"O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+"};

    /*
     * Matriz de compatibilidade ABO/Rh para hemácias, seguindo a convenção:
     * linhas = receptor e colunas = doador.
     * A célula [receptor][doador] indica se a bolsa é compatível com a requisição.
     */
    private static final boolean[][] MATRIZ_COMPATIBILIDADE = {
            {true, false, false, false, false, false, false, false}, // O-
            {true, true, false, false, false, false, false, false}, // O+
            {true, false, true, false, false, false, false, false}, // A-
            {true, true, true, true, false, false, false, false}, // A+
            {true, false, false, false, true, false, false, false}, // B-
            {true, true, false, false, true, true, false, false}, // B+
            {true, false, true, false, true, false, true, false}, // AB-
            {true, true, true, true, true, true, true, true} // AB+
    };

    public boolean testeABO(String tipoRequisicao, String tipoBolsa) {
        String receptor = normalizar(tipoRequisicao);
        String doador = normalizar(tipoBolsa);

        if (receptor == null || doador == null) {
            return false;
        }

        int indiceReceptor = indiceTipoSanguineo(receptor);
        int indiceDoador = indiceTipoSanguineo(doador);

        if (indiceReceptor < 0 || indiceDoador < 0) {
            return false;
        }

        return MATRIZ_COMPATIBILIDADE[indiceReceptor][indiceDoador];
    }

    private int indiceTipoSanguineo(String tipo) {
        for (int i = 0; i < TIPOS_SANGUINEOS.length; i++) {
            if (TIPOS_SANGUINEOS[i].equals(tipo)) {
                return i;
            }
        }
        return -1;
    }

    private String normalizar(String tipo) {
        if (tipo == null) {
            return null;
        }

        String normalizado = tipo.trim().toUpperCase(Locale.ROOT);
        return normalizado.isEmpty() ? null : normalizado;
    }
}
