package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.veiculotransporte;
import com.rotavital.tuguy.repository.veiculotransporterepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ThreadLocalRandom;

@Service
public class monitoramentotemperaturaservice {

    private final veiculotransporterepository repository;

    public monitoramentotemperaturaservice(veiculotransporterepository repository) {
        this.repository = repository;
    }

    @Scheduled(
            fixedRateString = "${rotavital.monitoramento.intervalo-ms:10000}",
            initialDelayString = "${rotavital.monitoramento.initial-delay-ms:1000}")
    @Transactional
    public void atualizarLeituras() {
        List<veiculotransporte> monitorados = repository.findAllByStatusIgnoreCase("EM_TRANSITO");
        LocalDateTime instanteLeitura = LocalDateTime.now();

        monitorados.forEach(veiculo -> {
            veiculo.setTemperaturaAtual(gerarTemperaturaSintetica());
            veiculo.setUltimaLeituraTemperatura(instanteLeitura);
        });

        repository.saveAll(monitorados);
    }

    @Transactional(readOnly = true)
    public List<veiculotransporte> listarMonitorados() {
        return repository.findAllByStatusIgnoreCase("EM_TRANSITO").stream()
                .sorted(Comparator.comparing(veiculotransporte::getCodigo,
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional
    public veiculotransporte registrarVeiculoEmTransporte(veiculotransporte veiculo) {
        if (veiculo == null || veiculo.getCodigo() == null || veiculo.getCodigo().isBlank()
                || veiculo.getDescricao() == null || veiculo.getDescricao().isBlank()) {
            throw new IllegalArgumentException("Identificação e descrição do veículo são obrigatórias.");
        }
        String codigo = veiculo.getCodigo().trim().toUpperCase(Locale.ROOT);
        String descricao = veiculo.getDescricao().trim();
        if (codigo.length() > 30 || descricao.length() > 120) {
            throw new IllegalArgumentException("Identificação ou descrição do veículo excede o limite.");
        }
        veiculotransporte registrado = repository.findByCodigoIgnoreCase(codigo).orElse(null);
        if (registrado != null && registrado.estaEmTransporte()) {
            throw new IllegalArgumentException("Este veículo já está registrado em um transporte ativo.");
        }
        if (registrado == null) {
            registrado = new veiculotransporte();
            registrado.setCodigo(codigo);
        }
        registrado.setDescricao(descricao);
        registrado.setStatus("EM_TRANSITO");
        registrado.setTemperaturaAtual(null);
        registrado.setUltimaLeituraTemperatura(null);
        return repository.save(registrado);
    }

    @Transactional
    public void concluirTransporte(Long veiculoId) {
        veiculotransporte veiculo = repository.findById(veiculoId)
                .orElseThrow(() -> new IllegalArgumentException("Veículo não encontrado: " + veiculoId));
        veiculo.setStatus("DISPONIVEL");
        repository.save(veiculo);
    }

    private BigDecimal gerarTemperaturaSintetica() {
        int decimosDeGrau = ThreadLocalRandom.current().nextInt(0, 81);
        return BigDecimal.valueOf(decimosDeGrau, 1);
    }
}
