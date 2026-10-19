package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface bolsahemocomponenterepository extends JpaRepository<bolsahemocomponente, Long> {
    long countByRequisicao_Id(Long requisicaoId);

    List<bolsahemocomponente> findAllByRequisicao_IdAndStatusIgnoreCase(Long requisicaoId, String status);

    List<bolsahemocomponente> findAllByStatusIgnoreCase(String status);
}
