package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*Lógica criada sob a perspectiva de lista First Expired, First Out (FEFO), 
priorizando a utilização das bolsas com menor data de validade e, em caso de empate, 
pelo código da bolsa.*/
@Service
public class bolsahemocomponentefefoservice {

    public List<bolsahemocomponente> listarEmOrdemFefo(List<bolsahemocomponente> bolsas) {
        return bolsas.stream()
                .filter(Objects::nonNull)
                .filter(bolsahemocomponente::estaDisponivel)
                .sorted(Comparator
                        .comparing(bolsahemocomponente::getDataValidade)
                        .thenComparing(bolsahemocomponente::getCodigo))
                .toList();
    }

    public Optional<bolsahemocomponente> selecionarProximaFefo(List<bolsahemocomponente> bolsas) {
        return listarEmOrdemFefo(bolsas).stream().findFirst();
    }

    public List<bolsahemocomponente> listarPorComponente(List<bolsahemocomponente> bolsas, String componente) {
        return listarEmOrdemFefo(bolsas).stream()
                .filter(bolsa -> bolsa.getComponente() != null
                        && bolsa.getComponente().equalsIgnoreCase(componente))
                .toList();
    }
}
