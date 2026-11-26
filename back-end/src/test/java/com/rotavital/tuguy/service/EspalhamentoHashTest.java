package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class EspalhamentoHashTest {

    private final EspalhamentoHash indice = new EspalhamentoHash();

    private bolsahemocomponente bolsa(String codigo, String tipo, String componente) {
        return new bolsahemocomponente(null, codigo, tipo, componente, 250,
                LocalDate.now().minusDays(1), LocalDate.now().plusDays(10),
                "DISPONIVEL", null);
    }

    @Test
    void consultaPorTipoEComponenteEncontraAsBolsasDaChave() {
        var oNegativa = bolsa("ON-1", "O-", "Hemácias");
        var oPositiva = bolsa("OP-1", "O+", "Hemácias");
        var plasma = bolsa("OP-P1", "O+", "Plasma");

        indice.inserir(oNegativa);
        indice.inserir(oPositiva);
        indice.inserir(plasma);

        assertEquals(List.of(oPositiva), indice.consultar("O+", "HEMACIAS"));
    }

    @Test
    void normalizaNomesDeComponenteParaCodigoCanonico() {
        var bolsa = bolsa("A1", "A+", "Hemácias");
        indice.inserir(bolsa);

        assertEquals(List.of(bolsa), indice.consultar(" a+ ", "HEMACIAS"));
        assertEquals(List.of(bolsa), indice.consultar("A+", "Hemacias"));
    }

    @Test
    void colisaoDeHashJavaNaoMisturaChavesDistintas() {
        var primeira = bolsa("1", "O+", "A|");
        var segunda = bolsa("2", "O+", "B]");
        assertEquals(EspalhamentoHash.chave("O+", "A|").hashCode(),
                EspalhamentoHash.chave("O+", "B]").hashCode());
        indice.reconstruir(List.of(primeira, segunda));

        assertEquals(List.of(primeira), indice.consultar("O+", "A|"));
        assertEquals(List.of(segunda), indice.consultar("O+", "B]"));
    }

    @Test
    void removerExcluiBolsaDoIndice() {
        var bolsa = bolsa("A1", "A+", "Hemácias");
        indice.inserir(bolsa);

        indice.remover(bolsa);

        assertTrue(indice.consultar("A+", "Hemácias").isEmpty());
    }

    @Test
    void reconstruirSubstituiOIndiceAnterior() {
        var antiga = bolsa("A1", "A+", "Hemácias");
        var nova = bolsa("B1", "B-", "Plasma");
        indice.inserir(antiga);

        indice.reconstruir(List.of(nova));

        assertTrue(indice.consultar("A+", "Hemácias").isEmpty());
        assertEquals(List.of(nova), indice.consultar("B-", "Plasma"));
    }
}
