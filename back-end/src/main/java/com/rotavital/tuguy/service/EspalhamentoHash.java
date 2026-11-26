package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.text.Normalizer;

/**
 * Índice hash do estoque por tipo sanguíneo e componente.
 *
 * Em C/C++, a chave seria convertida em índice por uma função de hash e os
 * elementos seriam armazenados em buckets encadeados. Em Java, um Map mantém
 * esse bucket diretamente e uma List contém as bolsas da mesma chave.
 */
@Service
public class EspalhamentoHash {

    private final Map<String, List<bolsahemocomponente>> buckets = new LinkedHashMap<>();

    public void reconstruir(List<bolsahemocomponente> bolsas) {
        buckets.clear();
        if (bolsas != null) {
            bolsas.forEach(this::inserir);
        }
    }

    public void inserir(bolsahemocomponente bolsa) {
        if (bolsa == null) {
            return;
        }
        List<bolsahemocomponente> bucket = buckets.computeIfAbsent(
                chave(bolsa.getTipoSanguineo(), bolsa.getComponente()), ignorada -> new ArrayList<>());
        boolean jaIndexada = bucket.stream().anyMatch(item -> item == bolsa
                || (item.getId() != null && item.getId().equals(bolsa.getId()))
                || (item.getCodigo() != null && item.getCodigo().equalsIgnoreCase(bolsa.getCodigo())));
        if (!jaIndexada) {
            bucket.add(bolsa);
        }
    }

    public void remover(bolsahemocomponente bolsa) {
        if (bolsa == null) {
            return;
        }
        String chave = chave(bolsa.getTipoSanguineo(), bolsa.getComponente());
        List<bolsahemocomponente> bucket = buckets.get(chave);
        if (bucket == null) {
            return;
        }
        bucket.removeIf(item -> item == bolsa
                || (item.getId() != null && item.getId().equals(bolsa.getId())));
        if (bucket.isEmpty()) {
            buckets.remove(chave);
        }
    }

    public List<bolsahemocomponente> consultar(String tipoSanguineo, String componente) {
        return List.copyOf(buckets.getOrDefault(
                chave(tipoSanguineo, componente), List.of()));
    }

    public List<bolsahemocomponente> consultarPorTipos(List<String> tiposSanguineos, String componente) {
        if (tiposSanguineos == null || tiposSanguineos.isEmpty()) {
            return List.of();
        }
        Map<String, bolsahemocomponente> resultado = new LinkedHashMap<>();
        for (String tipo : tiposSanguineos) {
            for (bolsahemocomponente bolsa : consultar(tipo, componente)) {
                String identidade = bolsa.getId() != null ? "ID:" + bolsa.getId()
                        : bolsa.getCodigo() != null ? "CODIGO:" + bolsa.getCodigo()
                        : "REF:" + System.identityHashCode(bolsa);
                resultado.putIfAbsent(identidade, bolsa);
            }
        }
        return List.copyOf(resultado.values());
    }

    public static int espalhamentoHash(int tamanho, String chave) {
        if (tamanho <= 0) {
            throw new IllegalArgumentException("O tamanho do índice hash deve ser positivo");
        }
        int hash = 0;
        if (chave != null) {
            for (char caractere : chave.toCharArray()) {
                hash = (31 * hash + caractere) & 0x7fffffff;
            }
        }
        return hash % tamanho;
    }

    public static String chave(String tipoSanguineo, String componente) {
        String tipo = tipoSanguineo == null ? "" : tipoSanguineo.trim().toUpperCase(Locale.ROOT);
        String comp = normalizarComponente(componente);
        return tipo + "|" + comp;
    }

    public static String normalizarComponente(String componente) {
        if (componente == null || componente.isBlank()) {
            return "";
        }
        String semAcentos = Normalizer.normalize(componente.trim(), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        if (semAcentos.contains("HEMACIA")) {
            return "HEMACIAS";
        }
        if (semAcentos.contains("PLASMA")) {
            return "PLASMA";
        }
        if (semAcentos.contains("PLAQUETA")) {
            return "PLAQUETAS";
        }
        return semAcentos.replaceAll("\\s+", "_");
    }
}
