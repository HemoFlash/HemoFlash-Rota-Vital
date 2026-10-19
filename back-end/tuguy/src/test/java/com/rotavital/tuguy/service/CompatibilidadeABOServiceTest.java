package com.rotavital.tuguy.service;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CompatibilidadeABOServiceTest {

    private final CompatibilidadeABOService service = new CompatibilidadeABOService();

    @Test
    void testeABOAplicaTodaMatrizDeCompatibilidadeParaHemacias() {
        Map<String, Set<String>> tiposDoadoresPorReceptor = Map.of(
                "O-", Set.of("O-"),
                "O+", Set.of("O-", "O+"),
                "A-", Set.of("O-", "A-"),
                "A+", Set.of("O-", "O+", "A-", "A+"),
                "B-", Set.of("O-", "B-"),
                "B+", Set.of("O-", "O+", "B-", "B+"),
                "AB-", Set.of("O-", "A-", "B-", "AB-"),
                "AB+", Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+")
        );
        Set<String> tiposSanguineos = Set.of("O-", "O+", "A-", "A+", "B-", "B+", "AB-", "AB+");

        tiposDoadoresPorReceptor.forEach((receptor, doadoresCompativeis) ->
                tiposSanguineos.forEach(doador ->
                        assertEquals(doadoresCompativeis.contains(doador), service.testeABO(receptor, doador),
                                () -> "Compatibilidade inesperada: receptor " + receptor + ", doador " + doador)));
    }

    @Test
    void testeABORejeitaTiposAusentesOuInvalidos() {
        assertFalse(service.testeABO("tipo inválido", "O-"));
        assertFalse(service.testeABO("A+", "tipo inválido"));
        assertFalse(service.testeABO(null, "O-"));
        assertFalse(service.testeABO("A+", null));
        assertFalse(service.testeABO("", "O-"));
        assertFalse(service.testeABO("A+", ""));
    }

    @Test
    void testeABONormalizaEspacosEMinusculas() {
        assertTrue(service.testeABO(" aB+ ", " o- "));
    }
}
