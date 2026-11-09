package com.rotavital.tuguy.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class bolsahemocomponentemonitoramentoTest {

    @Test
    void temperaturaNosLimitesDaFaixaEstaDentroDasCondicoesEsperadas() {
        bolsahemocomponente bolsa = new bolsahemocomponente();

        bolsa.setTemperaturaAtual(new BigDecimal("1.0"));
        assertTrue(bolsa.estaDentroDaFaixa());
        assertEquals("NORMAL", bolsa.getSituacaoTemperatura());

        bolsa.setTemperaturaAtual(new BigDecimal("7.0"));
        assertTrue(bolsa.estaDentroDaFaixa());
        assertEquals("NORMAL", bolsa.getSituacaoTemperatura());
    }

    @Test
    void temperaturasForaDaFaixaGeramAlertaELeituraAusenteFicaPendente() {
        bolsahemocomponente bolsa = new bolsahemocomponente();

        assertFalse(bolsa.estaDentroDaFaixa());
        assertEquals("AGUARDANDO LEITURA", bolsa.getSituacaoTemperatura());

        bolsa.setTemperaturaAtual(new BigDecimal("0.9"));
        assertFalse(bolsa.estaDentroDaFaixa());
        assertEquals("ALERTA", bolsa.getSituacaoTemperatura());

        bolsa.setTemperaturaAtual(new BigDecimal("7.1"));
        assertFalse(bolsa.estaDentroDaFaixa());
        assertEquals("ALERTA", bolsa.getSituacaoTemperatura());
    }

    @Test
    void bolsasContinuamMonitoradasEnquantoDisponiveisAlocadasOuEmTransito() {
        bolsahemocomponente bolsa = new bolsahemocomponente();

        bolsa.setStatus("DISPONIVEL");
        assertTrue(bolsa.estaEmMonitoramento());

        bolsa.setStatus("ALOCADA");
        assertTrue(bolsa.estaEmMonitoramento());

        bolsa.setStatus("EM_TRANSITO");
        assertTrue(bolsa.estaEmMonitoramento());

        bolsa.setStatus("DESCARTADA");
        assertFalse(bolsa.estaEmMonitoramento());
    }

    @Test
    void leituraComMaisDeVinteSegundosDeveSerSinalizadaComoDesatualizada() {
        bolsahemocomponente bolsa = new bolsahemocomponente();
        LocalDateTime agora = LocalDateTime.of(2026, 11, 9, 1, 0);

        bolsa.setUltimaLeituraTemperatura(agora.minusSeconds(21));
        assertTrue(bolsa.leituraTemperaturaDesatualizadaEm(agora));

        bolsa.setUltimaLeituraTemperatura(agora.minusSeconds(20));
        assertFalse(bolsa.leituraTemperaturaDesatualizadaEm(agora));

        bolsa.setUltimaLeituraTemperatura(null);
        assertFalse(bolsa.leituraTemperaturaDesatualizadaEm(agora));
    }
}
