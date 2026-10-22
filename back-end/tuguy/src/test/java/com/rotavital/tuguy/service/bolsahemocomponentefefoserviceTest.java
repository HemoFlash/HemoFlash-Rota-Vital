package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class bolsahemocomponentefefoserviceTest {

    private final bolsahemocomponentefefoservice service = new bolsahemocomponentefefoservice();
    private final LocalDate hoje = LocalDate.of(2026, 10, 22);

    private bolsahemocomponente bolsa(String codigo, String componente, int diasAteVencer, String status) {
        return new bolsahemocomponente(null, codigo, "O-", componente, 250,
                hoje.minusDays(2), hoje.plusDays(diasAteVencer), status, null);
    }

    @Test
    void listarEmOrdemFefoPriorizaValidadeMaisProxima() {
        var tardia = bolsa("B3", "Hemácias", 30, "DISPONIVEL");
        var proxima = bolsa("B1", "Hemácias", 5, "DISPONIVEL");
        var intermediaria = bolsa("B2", "Hemácias", 15, "DISPONIVEL");

        var resultado = service.listarEmOrdemFefo(List.of(tardia, intermediaria, proxima));

        assertEquals(List.of(proxima, intermediaria, tardia), resultado);
    }

    @Test
    void listarEmOrdemFefoDesempataPeloCodigoEIgnoraIndisponiveis() {
        var codigoB = bolsa("B", "Hemácias", 5, "DISPONIVEL");
        var codigoA = bolsa("A", "Hemácias", 5, "DISPONIVEL");
        var alocada = bolsa("C", "Hemácias", 1, "ALOCADA");

        var resultado = service.listarEmOrdemFefo(List.of(codigoB, alocada, codigoA));

        assertEquals(List.of(codigoA, codigoB), resultado);
    }

    @Test
    void selecionarProximaFefoRetornaPrimeiraDisponivelOuVazio() {
        var tardia = bolsa("B2", "Plasma", 20, "DISPONIVEL");
        var proxima = bolsa("B1", "Plasma", 3, "DISPONIVEL");

        assertEquals(proxima, service.selecionarProximaFefo(List.of(tardia, proxima)).orElseThrow());
        assertTrue(service.selecionarProximaFefo(List.of(bolsa("B3", "Plasma", 1, "ALOCADA"))).isEmpty());
    }

    @Test
    void listarPorComponenteFiltraSemDiferenciarMaiusculasEMinusculas() {
        var plasma = bolsa("P1", "Plasma", 4, "DISPONIVEL");
        var hemacias = bolsa("H1", "Hemácias", 2, "DISPONIVEL");

        assertEquals(List.of(plasma), service.listarPorComponente(List.of(hemacias, plasma), "pLaSmA"));
    }
    @Test
void bolsaVinculadaARequisicaoNaoDeveEstarDisponivel() {
    bolsahemocomponente bolsa = new bolsahemocomponente();

    bolsa.setCodigo("BOLSA-TDD-001");
    bolsa.setStatus("DISPONIVEL");
    bolsa.setDataValidade(java.time.LocalDate.now().plusDays(10));
    bolsa.setRequisicao(new com.rotavital.tuguy.model.requisicao());

    assertFalse(bolsa.estaDisponivel(),
            "Uma bolsa vinculada a uma requisicao nao pode estar disponivel");
    }
}
  
