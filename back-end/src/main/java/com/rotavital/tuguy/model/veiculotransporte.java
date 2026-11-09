package com.rotavital.tuguy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "veiculos_transporte")
public class veiculotransporte {

    private static final long LIMITE_LEITURA_DESATUALIZADA_SEGUNDOS = 20;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Identificação do veículo é obrigatória")
    @Column(nullable = false, unique = true)
    @Size(max = 30)
    private String codigo;

    @NotBlank(message = "Descrição do veículo é obrigatória")
    @Column(nullable = false)
    @Size(max = 120)
    private String descricao;

    @Column(nullable = false)
    private String status = "EM_TRANSITO";

    @Column(precision = 3, scale = 1)
    private BigDecimal temperaturaAtual;

    private LocalDateTime ultimaLeituraTemperatura;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public BigDecimal getTemperaturaAtual() {
        return temperaturaAtual;
    }

    public void setTemperaturaAtual(BigDecimal temperaturaAtual) {
        this.temperaturaAtual = temperaturaAtual;
    }

    public LocalDateTime getUltimaLeituraTemperatura() {
        return ultimaLeituraTemperatura;
    }

    public void setUltimaLeituraTemperatura(LocalDateTime ultimaLeituraTemperatura) {
        this.ultimaLeituraTemperatura = ultimaLeituraTemperatura;
    }

    public boolean estaEmTransporte() {
        return "EM_TRANSITO".equalsIgnoreCase(status);
    }

    public boolean estaDentroDaFaixa() {
        return temperaturaAtual != null
                && temperaturaAtual.compareTo(new BigDecimal("1.0")) >= 0
                && temperaturaAtual.compareTo(new BigDecimal("7.0")) <= 0;
    }

    public String getSituacaoTemperatura() {
        if (temperaturaAtual == null) {
            return "AGUARDANDO LEITURA";
        }
        return estaDentroDaFaixa() ? "NORMAL" : "ALERTA";
    }

    public boolean isLeituraTemperaturaDesatualizada() {
        return ultimaLeituraTemperatura != null
                && ultimaLeituraTemperatura.isBefore(
                        LocalDateTime.now().minusSeconds(LIMITE_LEITURA_DESATUALIZADA_SEGUNDOS));
    }

    public boolean leituraTemperaturaDesatualizadaEm(LocalDateTime instanteAtual) {
        return ultimaLeituraTemperatura != null
                && ultimaLeituraTemperatura.isBefore(
                        instanteAtual.minusSeconds(LIMITE_LEITURA_DESATUALIZADA_SEGUNDOS));
    }
}
