package com.rotavital.tuguy.ordenacao;

import com.rotavital.tuguy.model.requisicao;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Operação escolhida: ORDENAÇÃO DAS REQUISIÇÕES HOSPITALARES POR PRIORIDADE.
 *
 * Por que essa operação:
 * - Processa volume de dados (o histórico de requisições em escala nacional).
 * - O tempo é gasto comparando/ordenando em memória, não esperando banco/rede.
 * - É naturalmente particionável: o vetor pode ser dividido em fatias
 *   independentes, cada fatia ordenada sozinha, e o resultado agregado
 *   (merge) no final — não existe estado compartilhado durante o
 *   processamento, só na etapa final de junção.
 *
 * Big-O:
 * - Sequencial: ordenação por comparação -> O(n log n).
 * - Paralela (k threads, fatias de tamanho n/k):
 *     fase de ordenação: O((n/k) log(n/k)) de trabalho por thread, em paralelo
 *     fase de merge k-vias: O(n log k), sequencial
 *   Speedup teórico limitado pela parte sequencial (merge) — é o efeito
 *   descrito pela Lei de Amdahl: a partir de um certo k, o ganho marginal
 *   de mais threads cai porque o merge não paraleliza.
 */
@Service
public class ordenacaorequisicaoservice {

    /** Versão sequencial: baseline de comparação. Cópia defensiva + Collections.sort (O(n log n)). */
    public List<requisicao> ordenarSequencial(List<requisicao> dados) {
        List<requisicao> copia = new ArrayList<>(dados);
        copia.sort(prioridadeutil.COMPARADOR);
        return copia;
    }

    /**
     * Versão com threads: particiona os dados em {@code nThreads} fatias
     * contíguas, ordena cada fatia em paralelo (pool fixo de threads de
     * plataforma via ExecutorService) e agrega (merge k-vias) ao final.
     */
    public List<requisicao> ordenarComThreads(List<requisicao> dados, int nThreads) throws Exception {
        ExecutorService executor = Executors.newFixedThreadPool(nThreads);
        try {
            return ordenarParticionado(dados, nThreads, executor);
        } finally {
            executor.shutdown();
        }
    }

    /**
     * Versão bônus (Unidade 2 / destaque): mesmo algoritmo de particionamento,
     * mas usando virtual threads do Java 21 (uma virtual thread por fatia).
     * Como o trabalho de cada fatia é 100% CPU-bound (comparação e sort),
     * a expectativa é que o ganho sobre o pool de threads de plataforma
     * seja pequeno ou nulo — virtual threads só compensam claramente em
     * cargas I/O-bound (é o que os números medidos devem confirmar).
     */
    public List<requisicao> ordenarComVirtualThreads(List<requisicao> dados, int nFatias) throws Exception {
        try (ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor()) {
            return ordenarParticionado(dados, nFatias, executor);
        }
    }

    private List<requisicao> ordenarParticionado(List<requisicao> dados, int nFatias, ExecutorService executor) throws Exception {
        int n = dados.size();
        int tamanhoFatia = (int) Math.ceil((double) n / nFatias);

        List<List<requisicao>> fatias = new ArrayList<>();
        for (int inicio = 0; inicio < n; inicio += tamanhoFatia) {
            int fim = Math.min(inicio + tamanhoFatia, n);
            fatias.add(new ArrayList<>(dados.subList(inicio, fim)));
        }

        List<Future<List<requisicao>>> futuros = new ArrayList<>();
        for (List<requisicao> fatia : fatias) {
            futuros.add(executor.submit(() -> {
                fatia.sort(prioridadeutil.COMPARADOR); // cada thread processa a própria fatia
                return fatia;
            }));
        }

        List<List<requisicao>> fatiasOrdenadas = new ArrayList<>();
        for (Future<List<requisicao>> futuro : futuros) {
            fatiasOrdenadas.add(futuro.get()); // aguarda todas as threads
        }

        return mesclarKVias(fatiasOrdenadas, n); // agregação final
    }

    /** Junta k listas já ordenadas em uma única lista ordenada. O(n log k). */
    private List<requisicao> mesclarKVias(List<List<requisicao>> fatiasOrdenadas, int tamanhoTotal) {
        record apontador(requisicao item, int fatia, int indice) { }

        PriorityQueue<apontador> heap = new PriorityQueue<>(
                java.util.Comparator.comparing(apontador::item, prioridadeutil.COMPARADOR));

        for (int i = 0; i < fatiasOrdenadas.size(); i++) {
            if (!fatiasOrdenadas.get(i).isEmpty()) {
                heap.add(new apontador(fatiasOrdenadas.get(i).get(0), i, 0));
            }
        }

        List<requisicao> resultado = new ArrayList<>(tamanhoTotal);
        while (!heap.isEmpty()) {
            apontador atual = heap.poll();
            resultado.add(atual.item());
            List<requisicao> fatia = fatiasOrdenadas.get(atual.fatia());
            int proximoIndice = atual.indice() + 1;
            if (proximoIndice < fatia.size()) {
                heap.add(new apontador(fatia.get(proximoIndice), atual.fatia(), proximoIndice));
            }
        }
        return resultado;
    }

    /** Assinatura leve do resultado (sequência de ids) usada para provar que as versões batem. */
    public static List<Long> assinatura(List<requisicao> ordenado) {
        return ordenado.stream().map(requisicao::getId).toList();
    }
}
