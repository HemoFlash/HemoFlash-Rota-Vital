package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.hospital;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface hospitalrepository extends JpaRepository<hospital, Long> {
    Optional<hospital> findByCodigoIgnoreCase(String codigo);

    Optional<hospital> findByNomeIgnoreCase(String nome);

    List<hospital> findAllByMunicipioIgnoreCase(String municipio);

    List<hospital> findAllByCodigoStartingWithIgnoreCase(String prefixo);

    long countByMunicipioIgnoreCase(String municipio);

    boolean existsByCodigoIgnoreCase(String codigo);

    boolean existsByNomeIgnoreCase(String nome);
}
