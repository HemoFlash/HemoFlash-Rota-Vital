package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class indicadoresredeserviceTest {

    private final indicadoresredeservice service = new indicadoresredeservice();
    private int nextCode;

    @Test
    void calculaEstoquePorGrupoRhEComponenteEEmiteAlertas() {
        List<bolsahemocomponente> bolsas = new ArrayList<>();
        adicionarBolsas(bolsas, "B+", "Hemácias", 3);
        adicionarBolsas(bolsas, "B-", "Hemácias", 1);
        adicionarBolsas(bolsas, "A+", "Plasma", 1);
        adicionarBolsas(bolsas, "AB+", "Plasma", 1);
        bolsas.add(bolsa("O+", "Plaquetas", "ALOCADA"));
        bolsas.add(bolsa("AB-", "Hemácias", "VENCIDA"));

        var resultado = service.calcular(bolsas, List.of());

        assertEquals(6, resultado.bolsasDisponiveis());
        assertEquals(4, quantidadeDoTipo(resultado.estoquePorGrupoABO(), "B"));
        assertEquals("NORMAL", situacaoDoTipo(resultado.estoquePorGrupoABO(), "B"));
        assertEquals("BAIXO", situacaoDoTipo(resultado.estoquePorGrupoABO(), "A"));
        assertEquals(1, quantidadeDoTipo(resultado.estoquePorGrupoABO(), "AB"));
        assertEquals(1, quantidadeDoTipo(resultado.estoquePorGrupoABO(), "A"));
        assertEquals(1, quantidadeDoTipo(resultado.estoquePorTipoSanguineo(), "B-"));
        assertEquals("RESERVA BAIXA",
                situacaoDoTipo(resultado.estoquePorTipoSanguineo(), "B-"));
        assertEquals("INDISPONÍVEL",
                situacaoDoTipo(resultado.estoquePorTipoSanguineo(), "O-"));
        assertEquals(1, resultado.estoquePorComponente().stream()
                .filter(item -> item.tipoSanguineo().equals("A+")
                        && item.componente().equals("Plasma"))
                .findFirst().orElseThrow().quantidade());
        assertEquals("RESERVA BAIXA", resultado.estoquePorComponente().stream()
                .filter(item -> item.tipoSanguineo().equals("A+")
                        && item.componente().equals("Plasma"))
                .findFirst().orElseThrow().situacao());
        assertEquals("INDISPONÍVEL", resultado.estoquePorComponente().stream()
                .filter(item -> item.tipoSanguineo().equals("AB-")
                        && item.componente().equals("Hemácias"))
                .findFirst().orElseThrow().situacao());
    }

    @Test
    void contaRequisicoesPorTipoSanguineo() {
        List<requisicao> requisicoes = List.of(
                new requisicao(null, "Pedido 1", "Solicitação teste", "Alta", "B-"),
                new requisicao(null, "Pedido 2", "Solicitação teste", "Normal", " b- "),
                new requisicao(null, "Pedido 3", "Solicitação teste", "Baixa", "O+"));

        var resultado = service.calcular(List.of(), requisicoes);

        assertEquals(3, resultado.requisicoesRegistradas());
        assertEquals(2, resultado.demandaPorTipoSanguineo().stream()
                .filter(item -> item.tipoSanguineo().equals("B-"))
                .findFirst().orElseThrow().quantidade());
        assertEquals(1, resultado.demandaPorTipoSanguineo().stream()
                .filter(item -> item.tipoSanguineo().equals("O+"))
                .findFirst().orElseThrow().quantidade());
        assertEquals(0, resultado.demandaPorTipoSanguineo().stream()
                .filter(item -> item.tipoSanguineo().equals("AB+"))
                .findFirst().orElseThrow().quantidade());
    }

    private void adicionarBolsas(List<bolsahemocomponente> bolsas, String tipo,
                                String componente, int quantidade) {
        for (int i = 0; i < quantidade; i++) {
            bolsas.add(bolsa(tipo, componente, "DISPONIVEL"));
        }
    }

    private bolsahemocomponente bolsa(String tipo, String componente, String status) {
        return new bolsahemocomponente(null, tipo + componente + status + nextCode++,
                tipo, componente, 250, LocalDate.now(), LocalDate.now().plusDays(10), status, null);
    }

    private long quantidadeDoTipo(List<indicadoresredeservice.IndicadorEstoque> indicadores, String tipo) {
        return indicadores.stream()
                .filter(item -> item.tipo().equals(tipo))
                .findFirst().orElseThrow().quantidade();
    }

    private String situacaoDoTipo(List<indicadoresredeservice.IndicadorEstoque> indicadores, String tipo) {
        return indicadores.stream()
                .filter(item -> item.tipo().equals(tipo))
                .findFirst().orElseThrow().situacao();
    }
}
