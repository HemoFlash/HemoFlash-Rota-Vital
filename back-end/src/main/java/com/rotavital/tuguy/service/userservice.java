package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.user;
import com.rotavital.tuguy.repository.userrepository;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service 
public class userservice {
    
    private final userrepository Repository;

    public userservice(userrepository Repository) {
        this.Repository = Repository;
    }

    public user salvar(user user) {
        return Repository.save(user);
    }

    public user buscarPorId(Long id) {
        return Repository.findById(id).orElse(null);
    }

    public List<user> listarTodos() {
        return Repository.findAll();
    }

    public void remover(Long id) {
        Repository.deleteById(id);
    }   

    public user autenticar(String email, String senha){
        Optional<user> usuarioEncontrado = Repository.findByEmail(email);

        if (usuarioEncontrado.isPresent()){
            user usuario = usuarioEncontrado.get();

            if (usuario.getSenha().equals(senha)){
                return usuario;
            }
        }

        return null;


    }
}
