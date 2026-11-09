package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.veiculotransporte;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface veiculotransporterepository extends JpaRepository<veiculotransporte, Long> {
    Optional<veiculotransporte> findByCodigoIgnoreCase(String codigo);

    List<veiculotransporte> findAllByStatusIgnoreCase(String status);
}
