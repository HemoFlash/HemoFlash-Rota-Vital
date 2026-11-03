package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/*
 * Lógica criada sob a perspectiva de lista First Expired, First Out (FEFO),
 * priorizando a utilização das bolsas com menor data de validade e, em caso de
 * empate, pelo código da bolsa. A ordenação foi implementada usando inserção
 * ordenada para refletir o método exigido pela entrega de POO.
 */
@Service
public class bolsahemocomponentefefoservice {

    public List<bolsahemocomponente> listarEmOrdemFefo(List<bolsahemocomponente> bolsas) {
        List<bolsahemocomponente> ordenadas = new ArrayList<>();

        if (bolsas == null) {
            return ordenadas;
        }

        for (bolsahemocomponente bolsa : bolsas) {
            if (bolsa == null || !bolsa.estaDisponivel()) {
                continue;
            }
            inserirOrdenado(ordenadas, bolsa);
        }

        return ordenadas;
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

    private void inserirOrdenado(List<bolsahemocomponente> ordenadas, bolsahemocomponente bolsa) {
        int indice = 0;

        while (indice < ordenadas.size()
                && compararFefo(ordenadas.get(indice), bolsa) <= 0) {
            indice++;
        }

        ordenadas.add(indice, bolsa);
    }

    private int compararFefo(bolsahemocomponente bolsaAtual, bolsahemocomponente novaBolsa) {
        int comparacaoData = Objects.requireNonNullElse(bolsaAtual.getDataValidade(), novaBolsa.getDataValidade())
                .compareTo(Objects.requireNonNullElse(novaBolsa.getDataValidade(), bolsaAtual.getDataValidade()));

        if (comparacaoData != 0) {
            return comparacaoData;
        }

        String codigoAtual = bolsaAtual.getCodigo() == null ? "" : bolsaAtual.getCodigo().trim();
        String codigoNovo = novaBolsa.getCodigo() == null ? "" : novaBolsa.getCodigo().trim();
        return codigoAtual.compareToIgnoreCase(codigoNovo);
    }
}
