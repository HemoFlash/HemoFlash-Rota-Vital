package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.repository.bolsahemocomponenterepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class monitoramentotemperaturaservice {

    private final bolsahemocomponenterepository repository;

    public monitoramentotemperaturaservice(bolsahemocomponenterepository repository) {
        this.repository = repository;
    }

    @Scheduled(
            fixedRateString = "${rotavital.monitoramento.intervalo-ms:10000}",
            initialDelayString = "${rotavital.monitoramento.initial-delay-ms:1000}")
    @Transactional
    public void atualizarLeituras() {
        List<bolsahemocomponente> monitoradas = repository.findAll().stream()
                .filter(bolsahemocomponente::estaEmMonitoramento)
                .toList();
        LocalDateTime instanteLeitura = LocalDateTime.now();

        monitoradas.forEach(bolsa -> {
            bolsa.setTemperaturaAtual(gerarTemperaturaSintetica());
            bolsa.setUltimaLeituraTemperatura(instanteLeitura);
        });

        repository.saveAll(monitoradas);
    }

    @Transactional(readOnly = true)
    public List<bolsahemocomponente> listarMonitoradas() {
        return repository.findAll().stream()
                .filter(bolsahemocomponente::estaEmMonitoramento)
                .sorted(Comparator.comparing(bolsahemocomponente::getCodigo,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    private BigDecimal gerarTemperaturaSintetica() {
        int decimosDeGrau = ThreadLocalRandom.current().nextInt(0, 81);
        return BigDecimal.valueOf(decimosDeGrau, 1);
    }
}
