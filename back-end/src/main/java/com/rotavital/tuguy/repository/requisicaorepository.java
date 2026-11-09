package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.requisicao;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface requisicaorepository extends JpaRepository<requisicao, Long> {
    @Modifying
    @Query("update requisicao r set r.hospitalDestino = :novoCodigo where r.hospitalDestino = :codigoAntigo")
    int atualizarCodigoHospitalDestino(@Param("codigoAntigo") String codigoAntigo,
                                       @Param("novoCodigo") String novoCodigo);
}