package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.requisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface requisicaorepository extends JpaRepository<requisicao, Long> {
}