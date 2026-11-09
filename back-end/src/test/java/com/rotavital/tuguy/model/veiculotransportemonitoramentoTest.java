package com.rotavital.tuguy.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class veiculotransportemonitoramentoTest {

    @Test
    void temperaturaNosLimitesDaFaixaEstaDentroDasCondicoesEsperadas() {
        veiculotransporte veiculo = new veiculotransporte();

        veiculo.setTemperaturaAtual(new BigDecimal("1.0"));
        assertTrue(veiculo.estaDentroDaFaixa());
        assertEquals("NORMAL", veiculo.getSituacaoTemperatura());

        veiculo.setTemperaturaAtual(new BigDecimal("7.0"));
        assertTrue(veiculo.estaDentroDaFaixa());
        assertEquals("NORMAL", veiculo.getSituacaoTemperatura());
    }

    @Test
    void temperaturasForaDaFaixaGeramAlertaELeituraAusenteFicaPendente() {
        veiculotransporte veiculo = new veiculotransporte();

        assertFalse(veiculo.estaDentroDaFaixa());
        assertEquals("AGUARDANDO LEITURA", veiculo.getSituacaoTemperatura());

        veiculo.setTemperaturaAtual(new BigDecimal("0.9"));
        assertFalse(veiculo.estaDentroDaFaixa());
        assertEquals("ALERTA", veiculo.getSituacaoTemperatura());

        veiculo.setTemperaturaAtual(new BigDecimal("7.1"));
        assertFalse(veiculo.estaDentroDaFaixa());
        assertEquals("ALERTA", veiculo.getSituacaoTemperatura());
    }

    @Test
    void apenasVeiculosEmTransporteSaoMonitorados() {
        veiculotransporte veiculo = new veiculotransporte();

        veiculo.setStatus("EM_TRANSITO");
        assertTrue(veiculo.estaEmTransporte());

        veiculo.setStatus("DISPONIVEL");
        assertFalse(veiculo.estaEmTransporte());
    }

    @Test
    void leituraComMaisDeVinteSegundosDeveSerSinalizadaComoDesatualizada() {
        veiculotransporte veiculo = new veiculotransporte();
        LocalDateTime agora = LocalDateTime.of(2026, 11, 9, 1, 0);

        veiculo.setUltimaLeituraTemperatura(agora.minusSeconds(21));
        assertTrue(veiculo.leituraTemperaturaDesatualizadaEm(agora));

        veiculo.setUltimaLeituraTemperatura(agora.minusSeconds(20));
        assertFalse(veiculo.leituraTemperaturaDesatualizadaEm(agora));

        veiculo.setUltimaLeituraTemperatura(null);
        assertFalse(veiculo.leituraTemperaturaDesatualizadaEm(agora));
    }
}
