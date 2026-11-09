package com.rotavital.tuguy.model;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

public class PlanejamentoRota {

    private final String status;
    private final String mensagem;
    private final String hospitalOrigem;
    private final String hospitalDestino;
    private final String modalidadeTransporte;
    private final List<String> caminho;
    private final double distanciaKm;
    private final Duration duracao;
    private final Duration tempoMaximoTransporte;
    private final LocalDateTime chegadaEstimada;
    private final LocalDateTime limiteValidade;

    public PlanejamentoRota(String status, String mensagem, String hospitalOrigem, String hospitalDestino,
                            String modalidadeTransporte, List<String> caminho, double distanciaKm,
                            Duration duracao, Duration tempoMaximoTransporte,
                            LocalDateTime chegadaEstimada, LocalDateTime limiteValidade) {
        this.status = status;
        this.mensagem = mensagem;
        this.hospitalOrigem = hospitalOrigem;
        this.hospitalDestino = hospitalDestino;
        this.modalidadeTransporte = modalidadeTransporte;
        this.caminho = List.copyOf(caminho);
        this.distanciaKm = distanciaKm;
        this.duracao = duracao;
        this.tempoMaximoTransporte = tempoMaximoTransporte;
        this.chegadaEstimada = chegadaEstimada;
        this.limiteValidade = limiteValidade;
    }

    public String getStatus() {
        return status;
    }

    public String getMensagem() {
        return mensagem;
    }

    public String getHospitalOrigem() {
        return hospitalOrigem;
    }

    public String getHospitalDestino() {
        return hospitalDestino;
    }

    public String getModalidadeTransporte() {
        return modalidadeTransporte;
    }

    public List<String> getCaminho() {
        return caminho;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public Duration getDuracao() {
        return duracao;
    }

    public long getDuracaoMinutos() {
        return duracao == null ? 0 : duracao.toMinutes();
    }

    public long getTempoMaximoMinutos() {
        return tempoMaximoTransporte == null ? 0 : tempoMaximoTransporte.toMinutes();
    }

    public LocalDateTime getChegadaEstimada() {
        return chegadaEstimada;
    }

    public LocalDateTime getLimiteValidade() {
        return limiteValidade;
    }

    public boolean isRotaEncontrada() {
        return "ROTA_ENCONTRADA".equals(status);
    }

    public boolean isTemCaminho() {
        return !caminho.isEmpty();
    }
}
