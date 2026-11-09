package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Service
public class indicadoresredeservice {

    private static final List<String> TIPOS_SANGUINEOS =
            List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");
    private static final List<String> GRUPOS_ABO = List.of("A", "B", "AB", "O");

    public IndicadoresRede calcular(List<bolsahemocomponente> bolsas, List<requisicao> requisicoes) {
        Objects.requireNonNull(bolsas, "A lista de bolsas é obrigatória");
        Objects.requireNonNull(requisicoes, "A lista de requisições é obrigatória");

        Map<String, Long> estoquePorGrupo = contagemInicial(GRUPOS_ABO);
        Map<String, Long> estoquePorTipo = contagemInicial(TIPOS_SANGUINEOS);
        Map<String, String> componentesConhecidos = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        Map<ComponenteTipo, Long> estoquePorComponente = new TreeMap<>(
                Comparator.comparing(ComponenteTipo::tipoSanguineo)
                        .thenComparing(ComponenteTipo::componente, String.CASE_INSENSITIVE_ORDER));
        long bolsasDisponiveis = 0;

        for (bolsahemocomponente bolsa : bolsas) {
            if (bolsa == null) {
                continue;
            }

            String tipo = normalizarOuDesconhecido(bolsa.getTipoSanguineo());
            String componente = normalizarComponente(bolsa.getComponente());
            estoquePorTipo.putIfAbsent(tipo, 0L);
            componentesConhecidos.putIfAbsent(componente, componente);

            if (!bolsa.estaDisponivel()) {
                continue;
            }

            bolsasDisponiveis++;
            estoquePorTipo.merge(tipo, 1L, Long::sum);

            String grupo = grupoABO(tipo);
            if (grupo != null) {
                estoquePorGrupo.merge(grupo, 1L, Long::sum);
            }

            estoquePorComponente.merge(new ComponenteTipo(tipo, componente), 1L, Long::sum);
        }

        Map<String, Long> demandaPorTipo = contagemInicial(TIPOS_SANGUINEOS);
        long requisicoesRegistradas = 0;
        for (requisicao requisicao : requisicoes) {
            if (requisicao != null) {
                requisicoesRegistradas++;
                demandaPorTipo.merge(
                        normalizarOuDesconhecido(requisicao.getTipoSanguineo()), 1L, Long::sum);
            }
        }

        List<IndicadorEstoque> grupos = estoquePorGrupo.entrySet().stream()
                .map(item -> new IndicadorEstoque(item.getKey(), item.getValue(),
                        item.getValue() < 4 ? "BAIXO" : "NORMAL"))
                .toList();
        List<IndicadorEstoque> tipos = estoquePorTipo.entrySet().stream()
                .map(item -> new IndicadorEstoque(item.getKey(), item.getValue(), situacaoTipo(item.getValue())))
                .toList();
        List<IndicadorComponente> componentes = new ArrayList<>();
        for (String tipo : estoquePorTipo.keySet()) {
            for (String componente : componentesConhecidos.keySet()) {
                long quantidade = estoquePorComponente.getOrDefault(
                        new ComponenteTipo(tipo, componente), 0L);
                componentes.add(new IndicadorComponente(
                        tipo, componente, quantidade, situacaoTipo(quantidade)));
            }
        }
        List<IndicadorDemanda> demanda = demandaPorTipo.entrySet().stream()
                .map(item -> new IndicadorDemanda(item.getKey(), item.getValue()))
                .toList();

        return new IndicadoresRede(
                bolsasDisponiveis,
                requisicoesRegistradas,
                grupos,
                tipos,
                componentes,
                demanda);
    }

    private Map<String, Long> contagemInicial(List<String> chaves) {
        Map<String, Long> contagens = new LinkedHashMap<>();
        chaves.forEach(chave -> contagens.put(chave, 0L));
        return contagens;
    }

    private String situacaoTipo(long quantidade) {
        if (quantidade == 0) {
            return "INDISPONÍVEL";
        }
        return quantidade == 1 ? "RESERVA BAIXA" : "NORMAL";
    }

    private String grupoABO(String tipoSanguineo) {
        if (tipoSanguineo.startsWith("AB")) {
            return "AB";
        }
        String grupo = tipoSanguineo.substring(0, 1);
        return GRUPOS_ABO.contains(grupo) ? grupo : null;
    }

    private String normalizarOuDesconhecido(String valor) {
        if (valor == null || valor.isBlank()) {
            return "Não informado";
        }
        return valor.trim().toUpperCase(Locale.ROOT);
    }

    private String normalizarComponente(String componente) {
        if (componente == null || componente.isBlank()) {
            return "Não informado";
        }
        return componente.trim();
    }

    private record ComponenteTipo(String tipoSanguineo, String componente) {
    }

    public record IndicadoresRede(
            long bolsasDisponiveis,
            long requisicoesRegistradas,
            List<IndicadorEstoque> estoquePorGrupoABO,
            List<IndicadorEstoque> estoquePorTipoSanguineo,
            List<IndicadorComponente> estoquePorComponente,
            List<IndicadorDemanda> demandaPorTipoSanguineo) {
    }

    public record IndicadorEstoque(String tipo, long quantidade, String situacao) {
    }

    public record IndicadorComponente(
            String tipoSanguineo,
            String componente,
            long quantidade,
            String situacao) {
    }

    public record IndicadorDemanda(String tipoSanguineo, long quantidade) {
    }
}
