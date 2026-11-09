package com.rotavital.tuguy.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "hospitais")
public class hospital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Código do hospital é obrigatório")
    @Column(nullable = false, unique = true)
    @Size(max = 30)
    private String codigo;

    @NotBlank(message = "Nome do hospital é obrigatório")
    @Column(nullable = false, unique = true)
    @Size(max = 120)
    private String nome;

    @NotBlank(message = "Município é obrigatório")
    @Column(nullable = false)
    @Size(max = 100)
    private String municipio;

    public hospital() {
    }

    public hospital(String codigo, String nome, String municipio) {
        this.codigo = codigo;
        this.nome = nome;
        this.municipio = municipio;
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

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getMunicipio() {
        return municipio;
    }

    public void setMunicipio(String municipio) {
        this.municipio = municipio;
    }
}
