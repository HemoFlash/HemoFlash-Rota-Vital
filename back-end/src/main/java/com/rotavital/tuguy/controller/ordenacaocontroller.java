package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.ordenacao.geradordadossinteticosservice;
import com.rotavital.tuguy.ordenacao.linhabenchmark;
import com.rotavital.tuguy.ordenacao.ordenacaorequisicaoservice;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * Endpoint REST real (não script solto) para a operação de ordenação de
 * requisições por prioridade, nas versões sequencial, com threads e com
 * virtual threads (Java 21, bônus).
 *
 * Exemplos de uso (com a aplicação rodando em localhost:8080):
 *
 *  GET /api/v1/requisicoes/ordenar?quantidade=100000&versao=sequencial
 *  GET /api/v1/requisicoes/ordenar?quantidade=100000&versao=threads&threads=4
 *  GET /api/v1/requisicoes/ordenar?quantidade=100000&versao=virtual&threads=8
 *
 *  GET /api/v1/requisicoes/benchmark?quantidades=100000,1000000&threads=2,4,8
 *      -> gera a tabela completa (é essa resposta que vira a tabela/gráfico do relatório)
 */
@RestController
@RequestMapping("/api/v1/requisicoes")
public class ordenacaocontroller {

    private final geradordadossinteticosservice gerador;
    private final ordenacaorequisicaoservice servico;

    public ordenacaocontroller(geradordadossinteticosservice gerador, ordenacaorequisicaoservice servico) {
        this.gerador = gerador;
        this.servico = servico;
    }

    /** Uma única execução — útil para testar rapidamente uma combinação. */
    @GetMapping("/ordenar")
    public Object ordenar(@RequestParam(defaultValue = "100000") int quantidade,
                           @RequestParam(defaultValue = "sequencial") String versao,
                           @RequestParam(defaultValue = "4") int threads) throws Exception {

        List<requisicao> dados = gerador.gerar(quantidade);

        long inicio = System.nanoTime();
        List<requisicao> resultado = switch (versao) {
            case "threads" -> servico.ordenarComThreads(dados, threads);
            case "virtual" -> servico.ordenarComVirtualThreads(dados, threads);
            default -> servico.ordenarSequencial(dados);
        };
        double tempoMs = (System.nanoTime() - inicio) / 1_000_000.0;

        return new Object() {
            public final int qtd = quantidade;
            public final String v = versao;
            public final int nThreads = threads;
            public final double tempoEmMs = tempoMs;
            public final List<requisicao> primeirosCinco = resultado.subList(0, Math.min(5, resultado.size()));
        };
    }

    /**
     * Tabela completa de benchmark: para cada quantidade informada, roda a
     * versão sequencial (baseline) e a versão com threads para cada valor
     * de thread informado, calculando o speedup e conferindo se o
     * resultado bate exatamente com o sequencial.
     */
    @GetMapping("/benchmark")
    public List<linhabenchmark> benchmark(
            @RequestParam(defaultValue = "100000,1000000") String quantidades,
            @RequestParam(defaultValue = "2,4,8") String threads,
            @RequestParam(defaultValue = "false") boolean incluirVirtual) throws Exception {

        List<linhabenchmark> linhas = new ArrayList<>();

        for (String qtdStr : quantidades.split(",")) {
            int quantidade = Integer.parseInt(qtdStr.trim());
            List<requisicao> dados = gerador.gerar(quantidade); // mesmo dataset para todas as versões desta linha

            long inicioSeq = System.nanoTime();
            List<requisicao> resultadoSequencial = servico.ordenarSequencial(dados);
            double tempoSeqMs = (System.nanoTime() - inicioSeq) / 1_000_000.0;
            List<Long> assinaturaSequencial = ordenacaorequisicaoservice.assinatura(resultadoSequencial);

            linhas.add(new linhabenchmark(quantidade, "sequencial", 1, tempoSeqMs, 1.0, true));

            for (String tStr : threads.split(",")) {
                int nThreads = Integer.parseInt(tStr.trim());

                long inicioPar = System.nanoTime();
                List<requisicao> resultadoParalelo = servico.ordenarComThreads(dados, nThreads);
                double tempoParMs = (System.nanoTime() - inicioPar) / 1_000_000.0;
                boolean igual = assinaturaSequencial.equals(ordenacaorequisicaoservice.assinatura(resultadoParalelo));

                linhas.add(new linhabenchmark(quantidade, "threads", nThreads, tempoParMs,
                        tempoSeqMs / tempoParMs, igual));

                if (incluirVirtual) {
                    long inicioVirtual = System.nanoTime();
                    List<requisicao> resultadoVirtual = servico.ordenarComVirtualThreads(dados, nThreads);
                    double tempoVirtualMs = (System.nanoTime() - inicioVirtual) / 1_000_000.0;
                    boolean igualVirtual = assinaturaSequencial.equals(ordenacaorequisicaoservice.assinatura(resultadoVirtual));

                    linhas.add(new linhabenchmark(quantidade, "virtual", nThreads, tempoVirtualMs,
                            tempoSeqMs / tempoVirtualMs, igualVirtual));
                }
            }
        }

        return linhas;
    }
}
