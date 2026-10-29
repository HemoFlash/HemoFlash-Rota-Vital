package com.rotavital.tuguy.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "requisicoes")
public class requisicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Título é obrigatório")
    @Size(min = 3, max = 100, message = "O título deve ter entre 3 e 100 caracteres")
    private String titulo;

    @NotBlank(message = "Descrição é obrigatória")
    @Size(min = 3, max = 500, message = "A descrição deve ter entre 3 e 500 caracteres")
    private String descricao;

    @NotBlank(message = "Prioridade é obrigatória")
    private String prioridade;

    @NotBlank(message = "Tipo sanguíneo é obrigatório")
    private String tipoSanguineo;

    public requisicao() { }

    public requisicao(Long id, String titulo, String descricao, String prioridade) {
        this(id, titulo, descricao, prioridade, null);
    }

    public requisicao(Long id, String titulo, String descricao, String prioridade, String tipoSanguineo) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.prioridade = prioridade;
        this.tipoSanguineo = tipoSanguineo;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitulo() { return titulo; }
    public void setTitulo(String titulo) { this.titulo = titulo; }

    public String getDescricao() { return descricao; }
    public void setDescricao(String descricao) { this.descricao = descricao; }

    public String getPrioridade() { return prioridade; }
    public void setPrioridade(String prioridade) { this.prioridade = prioridade; }

    public String getTipoSanguineo() { return tipoSanguineo; }
    public void setTipoSanguineo(String tipoSanguineo) { this.tipoSanguineo = tipoSanguineo; }
}