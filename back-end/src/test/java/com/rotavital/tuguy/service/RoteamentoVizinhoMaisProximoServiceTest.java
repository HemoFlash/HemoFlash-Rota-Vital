package com.rotavital.tuguy.service;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class RoteamentoVizinhoMaisProximoServiceTest {

    private final RoteamentoVizinhoMaisProximoService service =
            new RoteamentoVizinhoMaisProximoService();

    @Test
    void escolheDiretamenteOTrechoDoVizinhoMaisProximo() {
        Map<String, List<RoteamentoVizinhoMaisProximoService.Trecho>> grafo = Map.of(
                "A", List.of(
                        new RoteamentoVizinhoMaisProximoService.Trecho("B", 12),
                        new RoteamentoVizinhoMaisProximoService.Trecho("C", 4)),
                "B", List.of(new RoteamentoVizinhoMaisProximoService.Trecho("C", 3)),
                "C", List.of()
        );

        var resultado = service.calcular("A", "C", grafo);

        assertEquals(List.of("A", "C"), resultado.caminho());
        assertEquals(4.0, resultado.distanciaKm());
    }

    @Test
    void retornaNuloQuandoNaoExisteCaminho() {
        Map<String, List<RoteamentoVizinhoMaisProximoService.Trecho>> grafo = Map.of(
                "A", List.of(),
                "B", List.of()
        );

        assertNull(service.calcular("A", "B", grafo));
    }

    @Test
    void origemIgualAoDestinoRetornaCaminhoZero() {
        var resultado = service.calcular("A", "A", Map.of());

        assertEquals(List.of("A"), resultado.caminho());
        assertEquals(0.0, resultado.distanciaKm());
    }

    @Test
    void percorreOsVizinhosMaisProximosAteODestino() {
        Map<String, List<RoteamentoVizinhoMaisProximoService.Trecho>> grafo = Map.of(
                "A", List.of(
                        new RoteamentoVizinhoMaisProximoService.Trecho("B", 2),
                        new RoteamentoVizinhoMaisProximoService.Trecho("C", 8)),
                "B", List.of(
                        new RoteamentoVizinhoMaisProximoService.Trecho("C", 3),
                        new RoteamentoVizinhoMaisProximoService.Trecho("D", 10)),
                "C", List.of(new RoteamentoVizinhoMaisProximoService.Trecho("D", 1)),
                "D", List.of()
        );

        var resultado = service.calcular("A", "D", grafo);

        assertEquals(List.of("A", "B", "C", "D"), resultado.caminho());
        assertEquals(6.0, resultado.distanciaKm());
    }

    @Test
    void podeNaoEncontrarDestinoQuandoVizinhoGulosoLevaAUmBecoSemSaida() {
        Map<String, List<RoteamentoVizinhoMaisProximoService.Trecho>> grafo = Map.of(
                "A", List.of(
                        new RoteamentoVizinhoMaisProximoService.Trecho("B", 1),
                        new RoteamentoVizinhoMaisProximoService.Trecho("C", 2)),
                "B", List.of(),
                "C", List.of(new RoteamentoVizinhoMaisProximoService.Trecho("D", 1)),
                "D", List.of(new RoteamentoVizinhoMaisProximoService.Trecho("E", 1)),
                "E", List.of()
        );

        assertNull(service.calcular("A", "E", grafo));
    }
}
