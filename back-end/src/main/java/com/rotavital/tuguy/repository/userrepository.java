package com.rotavital.tuguy.repository;

import com.rotavital.tuguy.model.user;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;


@Repository 
public interface userrepository extends JpaRepository<user, Long>{


    Optional <user> findByEmail(String email);

}
