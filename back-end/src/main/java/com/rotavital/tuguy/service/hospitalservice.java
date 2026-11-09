package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.hospital;
import com.rotavital.tuguy.repository.hospitalrepository;
import com.rotavital.tuguy.repository.requisicaorepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class hospitalservice implements CommandLineRunner {

    private static final Pattern NAO_LETRAS = Pattern.compile("[^A-Z]+");
    private final hospitalrepository repository;
    private final requisicaorepository requisicaoRepository;

    public hospitalservice(hospitalrepository repository, requisicaorepository requisicaoRepository) {
        this.repository = repository;
        this.requisicaoRepository = requisicaoRepository;
    }

    @Override
    @Transactional
    public void run(String... args) {
        inserirOuAtualizar("REC0001", "Hospital Recife", "Recife");
        inserirOuAtualizar("OLI0001", "Hospital Olinda", "Olinda");
        inserirOuAtualizar("CAR0001", "Hospital Caruaru", "Caruaru");
        inserirOuAtualizar("PET0001", "Hospital Petrolina", "Petrolina");
    }

    @Transactional(readOnly = true)
    public List<hospital> listarTodos() {
        return repository.findAll().stream()
                .sorted((a, b) -> a.getNome().compareToIgnoreCase(b.getNome()))
                .toList();
    }

    @Transactional(readOnly = true)
    public hospital buscarPorId(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado: " + id));
    }

    @Transactional
    public hospital cadastrar(hospital hospital) {
        if (hospital == null || hospital.getNome() == null || hospital.getNome().isBlank()
                || hospital.getMunicipio() == null || hospital.getMunicipio().isBlank()) {
            throw new IllegalArgumentException("Nome e município do hospital são obrigatórios.");
        }
        String nome = hospital.getNome().trim();
        String municipio = hospital.getMunicipio().trim();
        if (nome.length() > 120 || municipio.length() > 100) {
            throw new IllegalArgumentException("Nome ou município excede o limite de caracteres.");
        }
        if (repository.existsByNomeIgnoreCase(nome)) {
            throw new IllegalArgumentException("Já existe um hospital cadastrado com este nome.");
        }
        hospital.setNome(nome);
        hospital.setMunicipio(municipio);
        hospital.setCodigo(gerarProximoCodigo(municipio));
        return repository.save(hospital);
    }

    @Transactional(readOnly = true)
    public hospital buscarPorCodigo(String codigo) {
        return repository.findByCodigoIgnoreCase(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Hospital não encontrado: " + codigo));
    }

    @Transactional(readOnly = true)
    public boolean existePorCodigo(String codigo) {
        return codigo != null && repository.existsByCodigoIgnoreCase(codigo);
    }

    private void inserirOuAtualizar(String codigo, String nome, String municipio) {
        hospital existente = repository.findByNomeIgnoreCase(nome).orElse(null);
        if (existente != null) {
            if (!codigo.equalsIgnoreCase(existente.getCodigo())) {
                String codigoAntigo = existente.getCodigo();
                existente.setCodigo(codigo);
                repository.save(existente);
                requisicaoRepository.atualizarCodigoHospitalDestino(codigoAntigo, codigo);
            }
        } else if (!repository.existsByCodigoIgnoreCase(codigo)) {
            repository.save(new hospital(codigo, nome, municipio));
        }
    }

    private String gerarProximoCodigo(String municipio) {
        String semAcentos = Normalizer.normalize(municipio, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toUpperCase(Locale.ROOT);
        String letras = NAO_LETRAS.matcher(semAcentos).replaceAll("");
        if (letras.length() < 3) {
            throw new IllegalArgumentException("O município precisa ter pelo menos três letras para gerar o código.");
        }
        String prefixo = letras.substring(0, 3);
        Pattern codigoSequencial = Pattern.compile("^" + Pattern.quote(prefixo) + "(\\d{4})$");
        int maiorSequencia = repository.findAllByCodigoStartingWithIgnoreCase(prefixo).stream()
                .map(hospital::getCodigo)
                .map(codigoSequencial::matcher)
                .filter(Matcher::matches)
                .mapToInt(matcher -> Integer.parseInt(matcher.group(1)))
                .max()
                .orElse(0);
        if (maiorSequencia >= 9999) {
            throw new IllegalArgumentException("A sequência de códigos deste município atingiu o limite.");
        }
        return prefixo + String.format(Locale.ROOT, "%04d", maiorSequencia + 1);
    }
}
