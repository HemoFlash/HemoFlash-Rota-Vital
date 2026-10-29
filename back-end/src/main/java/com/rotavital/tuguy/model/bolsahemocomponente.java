package com.rotavital.tuguy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

@Entity
@Table(name = "bolsas_hemocomponentes")
public class bolsahemocomponente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código da bolsa é obrigatório")
    @Column(unique = true)
    private String codigo;

    @NotBlank(message = "Tipo sanguíneo é obrigatório")
    private String tipoSanguineo;

    @NotBlank(message = "Componente é obrigatório")
    private String componente;

    @NotNull(message = "Quantidade é obrigatória")
    @Min(value = 1, message = "Quantidade deve ser maior que zero")
    private Integer quantidadeMl;

    @NotNull(message = "Data de coleta é obrigatória")
    private LocalDate dataColeta;

    @NotNull(message = "Data de validade é obrigatória")
    private LocalDate dataValidade;

    @NotBlank(message = "Status é obrigatório")
    private String status;

    @ManyToOne(fetch = FetchType.LAZY, optional = true)
    @JoinColumn(name = "requisicao_id", nullable = true)
    private requisicao requisicao;

    public bolsahemocomponente() {
    }

    public bolsahemocomponente(Long id, String codigo, String tipoSanguineo, String componente,
                               Integer quantidadeMl, LocalDate dataColeta, LocalDate dataValidade,
                               String status, requisicao requisicao) {
        this.id = id;
        this.codigo = codigo;
        this.tipoSanguineo = tipoSanguineo;
        this.componente = componente;
        this.quantidadeMl = quantidadeMl;
        this.dataColeta = dataColeta;
        this.dataValidade = dataValidade;
        this.status = status;
        this.requisicao = requisicao;
    }

 public boolean estaDisponivel() {
    return "DISPONIVEL".equalsIgnoreCase(this.status)
            && this.dataValidade != null
            && !this.dataValidade.isBefore(LocalDate.now())
            && this.requisicao == null;
}

    public boolean estaVencida() {
        return this.dataValidade != null && this.dataValidade.isBefore(LocalDate.now());
    }

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

    public String getTipoSanguineo() {
        return tipoSanguineo;
    }

    public void setTipoSanguineo(String tipoSanguineo) {
        this.tipoSanguineo = tipoSanguineo;
    }

    public String getComponente() {
        return componente;
    }

    public void setComponente(String componente) {
        this.componente = componente;
    }

    public Integer getQuantidadeMl() {
        return quantidadeMl;
    }

    public void setQuantidadeMl(Integer quantidadeMl) {
        this.quantidadeMl = quantidadeMl;
    }

    public LocalDate getDataColeta() {
        return dataColeta;
    }

    public void setDataColeta(LocalDate dataColeta) {
        this.dataColeta = dataColeta;
    }

    public LocalDate getDataValidade() {
        return dataValidade;
    }

    public void setDataValidade(LocalDate dataValidade) {
        this.dataValidade = dataValidade;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public requisicao getRequisicao() {
        return requisicao;
    }

    public void setRequisicao(requisicao requisicao) {
        this.requisicao = requisicao;
    }
}
