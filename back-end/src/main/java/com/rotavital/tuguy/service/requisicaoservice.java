package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.repository.requisicaorepository;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class requisicaoservice {

    private final requisicaorepository Repository;
    private final FilaRequisicoes fila = new FilaRequisicoes();

    public requisicaoservice(requisicaorepository Repository) {
        this.Repository = Repository;
    }

    public requisicao salvar(requisicao requisicao) {
        return Repository.save(requisicao);
    }

    public List<requisicao> listarTodos() {
        return Repository.findAll();
    }

    public requisicao buscarPorId(Long id) {
        return Repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Requisição não encontrada com o id: " + id));
    }

    public void deletar(Long id) {
        Repository.deleteById(id);
    }

    public requisicao consultarProxima() {
        return fila.consultarInicio();
    }

    public requisicao retirarProxima() {
        return fila.desenfileirar();
    }
}
