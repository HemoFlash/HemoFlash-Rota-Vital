package com.rotavital.tuguy.ordenacao;

import com.rotavital.tuguy.model.requisicao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Gera requisicoes sintéticas em memória para o benchmark.
 *
 * Importante: os objetos NÃO são salvos no H2. Isso é proposital — o
 * roteiro pede uma operação em que o tempo é gasto CALCULANDO sobre os
 * dados, não esperando banco/rede (item 2 do roteiro). Gerar em memória
 * isola exatamente o custo de CPU do algoritmo de ordenação.
 */
@Service
public class geradordadossinteticosservice {

    private static final String[] PRIORIDADES = {"alta", "media", "baixa"};

    /** Gera {@code quantidade} requisicoes com seed fixa (dados reprodutíveis entre execuções). */
    public List<requisicao> gerar(int quantidade) {
        Random random = new Random(42L); // seed fixa: sequencial e paralela partem do mesmo dataset
        List<requisicao> lista = new ArrayList<>(quantidade);
        for (long id = 1; id <= quantidade; id++) {
            String prioridade = PRIORIDADES[random.nextInt(PRIORIDADES.length)];
            lista.add(new requisicao(
                    id,
                    "Requisicao hospitalar #" + id,
                    "Requisicao sintetica gerada para benchmark de ordenacao por prioridade.",
                    prioridade
            ));
        }
        return lista;
    }
}
