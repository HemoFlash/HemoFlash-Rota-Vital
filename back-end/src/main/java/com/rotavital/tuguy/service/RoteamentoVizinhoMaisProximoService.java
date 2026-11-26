package com.rotavital.tuguy.service;

import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Busca didática pelo vizinho mais próximo exigida pela Unidade 2.
 *
 * Em C/C++, os vértices visitados são normalmente vetores booleanos e os
 * menores candidatos são encontrados por varredura. Java usa Set e objetos,
 * preservando a mesma decisão: escolher o menor trecho disponível.
 */
@Service
public class RoteamentoVizinhoMaisProximoService {

    public Resultado calcular(String origem, String destino,
                              Map<String, List<Trecho>> grafo) {
        if (origem == null || destino == null) {
            return null;
        }
        if (origem.equals(destino)) {
            return new Resultado(List.of(origem), 0.0);
        }
        if (grafo == null) {
            return null;
        }

        Set<String> visitados = new HashSet<>();
        List<String> caminho = new ArrayList<>();
        caminho.add(origem);
        visitados.add(origem);
        String atual = origem;
        double distancia = 0.0;

        while (!atual.equals(destino)) {
            Trecho vizinho = null;
            for (Trecho trecho : grafo.getOrDefault(atual, List.of())) {
                if (!visitados.contains(trecho.destino())
                        && (vizinho == null || trecho.distanciaKm() < vizinho.distanciaKm())) {
                    vizinho = trecho;
                }
            }
            if (vizinho == null) {
                return null;
            }
            atual = vizinho.destino();
            visitados.add(atual);
            caminho.add(atual);
            distancia += vizinho.distanciaKm();
        }

        return new Resultado(List.copyOf(caminho), distancia);
    }

    public record Trecho(String destino, double distanciaKm) {
    }

    public record Resultado(List<String> caminho, double distanciaKm) {
    }
}
