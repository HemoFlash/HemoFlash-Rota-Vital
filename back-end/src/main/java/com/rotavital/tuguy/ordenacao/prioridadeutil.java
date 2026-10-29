package com.rotavital.tuguy.ordenacao;

import com.rotavital.tuguy.model.requisicao;

import java.text.Normalizer;
import java.util.Comparator;

/**
 * Traduz o texto livre do campo "prioridade" de uma requisicao em um rank
 * numérico (menor = mais urgente) e expõe o comparador único usado por
 * TODAS as versões do algoritmo de ordenação (sequencial, com threads e
 * com virtual threads).
 *
 * Usar o MESMO Comparator nas três versões é o que garante que elas
 * devolvam exatamente a mesma resposta: como o desempate é feito por id
 * (único), a ordenação final é totalmente determinística, não importa em
 * quantas fatias os dados foram processados nem em que ordem as threads
 * terminaram.
 */
public final class prioridadeutil {

    private prioridadeutil() { }

    /** ALTA = 0 (mais urgente) ... BAIXA = 2 (menos urgente). Desconhecida = 3. */
    public static int rank(String prioridade) {
        if (prioridade == null) {
            return 3;
        }
        String normalizado = Normalizer.normalize(prioridade.trim().toLowerCase(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", ""); // remove acentos (média -> media)

        return switch (normalizado) {
            case "alta", "urgente", "critica" -> 0;
            case "media" -> 1;
            case "baixa" -> 2;
            default -> 3;
        };
    }

    /** Comparador oficial: prioridade (rank) e, em empate, id crescente. */
    public static final Comparator<requisicao> COMPARADOR =
            Comparator.comparingInt((requisicao r) -> rank(r.getPrioridade()))
                    .thenComparingLong(requisicao::getId);
}
