package com.rotavital.tuguy.ordenacao;

public class linhabenchmark {

    private int quantidade;
    private String versao;      // sequencial | threads | virtual
    private int threads;        // 1 para sequencial
    private double tempoMs;
    private double speedup;     // tempoSequencial / tempoDestaVersao
    private boolean resultadoIdenticoAoSequencial;

    public linhabenchmark() { }

    public linhabenchmark(int quantidade, String versao, int threads, double tempoMs,
                           double speedup, boolean resultadoIdenticoAoSequencial) {
        this.quantidade = quantidade;
        this.versao = versao;
        this.threads = threads;
        this.tempoMs = tempoMs;
        this.speedup = speedup;
        this.resultadoIdenticoAoSequencial = resultadoIdenticoAoSequencial;
    }

    public int getQuantidade() { return quantidade; }
    public String getVersao() { return versao; }
    public int getThreads() { return threads; }
    public double getTempoMs() { return tempoMs; }
    public double getSpeedup() { return speedup; }
    public boolean isResultadoIdenticoAoSequencial() { return resultadoIdenticoAoSequencial; }
}
