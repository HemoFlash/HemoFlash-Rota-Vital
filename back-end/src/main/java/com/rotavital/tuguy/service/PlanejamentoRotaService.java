package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.PlanejamentoRota;
import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.hospital;
import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.repository.bolsahemocomponenterepository;
import com.rotavital.tuguy.repository.hospitalrepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Normalizer;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.PriorityQueue;

@Service
public class PlanejamentoRotaService {

    private static final String REFRIGERADO = "REFRIGERADO";
    private static final String NAO_REFRIGERADO = "NAO_REFRIGERADO";
    private static final int VELOCIDADE_REFRIGERADO_KMH = 50;
    private static final int VELOCIDADE_NAO_REFRIGERADO_KMH = 70;

    private final bolsahemocomponenterepository bolsaRepository;
    private final hospitalrepository hospitalRepository;

    public PlanejamentoRotaService(bolsahemocomponenterepository bolsaRepository,
                                   hospitalrepository hospitalRepository) {
        this.bolsaRepository = bolsaRepository;
        this.hospitalRepository = hospitalRepository;
    }

    @Transactional(readOnly = true)
    public PlanejamentoRota planejar(requisicao requisicao, String modalidade) {
        if (requisicao == null || requisicao.getId() == null) {
            throw new IllegalArgumentException("Requisição válida é obrigatória para planejar a rota.");
        }

        String transporte = normalizarModalidade(modalidade);
        List<bolsahemocomponente> bolsas =
                bolsaRepository.findAllByRequisicao_IdAndStatusIgnoreCase(requisicao.getId(), "ALOCADA");
        if (bolsas.isEmpty()) {
            return resultado("SEM_BOLSA", "Nenhuma bolsa está alocada a esta requisição.",
                    null, null, transporte, List.of(), 0, null, null, null, null);
        }

        String codigoDestino = requisicao.getHospitalDestino();
        hospital destino = codigoDestino == null || codigoDestino.isBlank()
                ? null
                : hospitalRepository.findByCodigoIgnoreCase(codigoDestino).orElse(null);
        if (destino == null) {
            return resultado("SEM_DESTINO", "O hospital de destino não está cadastrado.",
                    null, requisicao.getHospitalDestino(), transporte, List.of(), 0, null, null, null, null);
        }

        bolsahemocomponente bolsa = bolsas.get(0);
        hospital origem = bolsa.getHospitalEstoque();
        if (origem == null) {
            return resultado("SEM_ORIGEM",
                    "A bolsa não possui hospital de estoque informado. Atualize o cadastro da bolsa.",
                    null, destino.getNome(), transporte, List.of(), 0, null, null, null,
                    limiteValidade(bolsa.getDataValidade()));
        }

        LocalDateTime agora = LocalDateTime.now();
        LocalDateTime validade = limiteValidade(bolsa.getDataValidade());
        if (validade == null || !agora.isBefore(validade)) {
            return resultado("HEMOCOMPONENTE_VENCIDO",
                    "O hemocomponente já está vencido e não pode ser transportado.",
                    origem.getNome(), destino.getNome(), transporte, List.of(), 0, null, null, null, validade);
        }

        Duration tolerancia = toleranciaTransporte(bolsa.getComponente(), transporte);
        if (tolerancia == null) {
            return resultado("COMPONENTE_NAO_SUPORTADO",
                    "Não há limite de transporte demonstrativo configurado para este componente.",
                    origem.getNome(), destino.getNome(), transporte, List.of(), 0, null, null, null, validade);
        }

        Caminho caminho = buscarCaminho(origem.getCodigo(), destino.getCodigo());
        if (caminho == null) {
            return resultado("SEM_ROTA",
                    "Não foi encontrada rota cadastrada entre os hospitais com os dados sintéticos disponíveis.",
                    origem.getNome(), destino.getNome(), transporte, List.of(), 0, null, tolerancia, null, validade);
        }

        int velocidade = REFRIGERADO.equals(transporte)
                ? VELOCIDADE_REFRIGERADO_KMH : VELOCIDADE_NAO_REFRIGERADO_KMH;
        long minutos = (long) Math.ceil(caminho.distanciaKm() * 60 / velocidade);
        Duration duracao = Duration.ofMinutes(minutos);
        LocalDateTime chegada = agora.plus(duracao);
        if (duracao.compareTo(tolerancia) > 0) {
            return resultado("TEMPO_TRANSPORTE_EXCEDIDO",
                    "A duração estimada supera o tempo sintético permitido para este componente e transporte.",
                    origem.getNome(), destino.getNome(), transporte, caminho.hospitais(),
                    caminho.distanciaKm(), duracao, tolerancia, chegada, validade);
        }
        if (chegada.isAfter(validade)) {
            return resultado("VENCIMENTO_NO_TRAJETO",
                    "O hemocomponente venceria antes da chegada ao hospital de destino.",
                    origem.getNome(), destino.getNome(), transporte, caminho.hospitais(),
                    caminho.distanciaKm(), duracao, tolerancia, chegada, validade);
        }

        return resultado("ROTA_ENCONTRADA",
                "Rota encontrada. A duração respeita o limite de transporte e a data de validade.",
                origem.getNome(), destino.getNome(), transporte, caminho.hospitais(),
                caminho.distanciaKm(), duracao, tolerancia, chegada, validade);
    }

    private Caminho buscarCaminho(String origem, String destino) {
        if (origem.equals(destino)) {
            hospital atual = hospitalRepository.findByCodigoIgnoreCase(origem).orElseThrow();
            return new Caminho(List.of(atual.getNome()), 0);
        }

        Map<String, List<Trecho>> grafo = criarGrafo();
        Map<String, Double> distancias = new HashMap<>();
        Map<String, String> anteriores = new HashMap<>();
        PriorityQueue<No> fila = new PriorityQueue<>(Comparator.comparingDouble(No::distanciaKm));
        distancias.put(origem, 0.0);
        fila.add(new No(origem, 0));

        while (!fila.isEmpty()) {
            No atual = fila.poll();
            if (atual.distanciaKm() > distancias.getOrDefault(atual.codigo(), Double.POSITIVE_INFINITY)) {
                continue;
            }
            if (atual.codigo().equals(destino)) {
                break;
            }
            for (Trecho trecho : grafo.getOrDefault(atual.codigo(), List.of())) {
                double total = atual.distanciaKm() + trecho.distanciaKm();
                if (total < distancias.getOrDefault(trecho.destino(), Double.POSITIVE_INFINITY)) {
                    distancias.put(trecho.destino(), total);
                    anteriores.put(trecho.destino(), atual.codigo());
                    fila.add(new No(trecho.destino(), total));
                }
            }
        }

        Double distancia = distancias.get(destino);
        if (distancia == null) {
            return null;
        }

        List<String> codigos = new ArrayList<>();
        for (String local = destino; local != null; local = anteriores.get(local)) {
            codigos.add(local);
        }
        Collections.reverse(codigos);
        List<String> hospitais = codigos.stream()
                .map(codigo -> hospitalRepository.findByCodigoIgnoreCase(codigo)
                        .map(hospital::getNome)
                        .orElse(codigo))
                .toList();
        return new Caminho(hospitais, distancia);
    }

    private Map<String, List<Trecho>> criarGrafo() {
        Map<String, List<Trecho>> grafo = new HashMap<>();
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital da Restauração (HR)", 1.4);
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital Santa Joana Recife", 2.1);
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital Esperança Recife", 1.2);
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco",
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", 1.6);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital da Restauração (HR)", "Hospital Santa Joana Recife", 1.2);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital da Restauração (HR)", "Hospital Esperança Recife", 1.8);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital da Restauração (HR)",
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", 2.2);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital Santa Joana Recife", "Hospital Esperança Recife", 2.4);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital Santa Joana Recife",
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", 2.8);
        adicionarTrechoEntreHospitais(grafo,
                "Hospital Esperança Recife",
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", 1.1);

        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital Olinda", 18);
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital Caruaru", 135);
        adicionarTrechoEntreHospitais(grafo,
                "Real Hospital Português de Beneficência em Pernambuco", "Hospital Petrolina", 730);
        adicionarTrechoEntreHospitais(grafo, "Hospital da Restauração (HR)", "Hospital Olinda", 17);
        adicionarTrechoEntreHospitais(grafo, "Hospital da Restauração (HR)", "Hospital Caruaru", 137);
        adicionarTrechoEntreHospitais(grafo, "Hospital da Restauração (HR)", "Hospital Petrolina", 735);
        adicionarTrechoEntreHospitais(grafo, "Hospital Santa Joana Recife", "Hospital Olinda", 19);
        adicionarTrechoEntreHospitais(grafo, "Hospital Santa Joana Recife", "Hospital Caruaru", 138);
        adicionarTrechoEntreHospitais(grafo, "Hospital Santa Joana Recife", "Hospital Petrolina", 738);
        adicionarTrechoEntreHospitais(grafo, "Hospital Esperança Recife", "Hospital Olinda", 18.5);
        adicionarTrechoEntreHospitais(grafo, "Hospital Esperança Recife", "Hospital Caruaru", 136);
        adicionarTrechoEntreHospitais(grafo, "Hospital Esperança Recife", "Hospital Petrolina", 732);
        adicionarTrechoEntreHospitais(grafo,
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", "Hospital Olinda", 20);
        adicionarTrechoEntreHospitais(grafo,
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", "Hospital Caruaru", 140);
        adicionarTrechoEntreHospitais(grafo,
                "IMIP - Instituto de Medicina Integral Professor Fernando Figueira", "Hospital Petrolina", 740);
        adicionarTrechoBidirecional(grafo, "OLI0001", "CAR0001", 145);
        adicionarTrechoBidirecional(grafo, "CAR0001", "PET0001", 590);
        return grafo;
    }

    private void adicionarTrechoEntreHospitais(Map<String, List<Trecho>> grafo,
                                                String nomeOrigem, String nomeDestino, double distanciaKm) {
        hospital origem = hospitalRepository.findByNomeIgnoreCase(nomeOrigem).orElse(null);
        hospital destino = hospitalRepository.findByNomeIgnoreCase(nomeDestino).orElse(null);
        if (origem != null && destino != null) {
            adicionarTrechoBidirecional(grafo, origem.getCodigo(), destino.getCodigo(), distanciaKm);
        }
    }

    private void adicionarTrechoBidirecional(Map<String, List<Trecho>> grafo,
                                             String origem, String destino, double distanciaKm) {
        if (hospitalRepository.existsByCodigoIgnoreCase(origem)
                && hospitalRepository.existsByCodigoIgnoreCase(destino)) {
            grafo.computeIfAbsent(origem, chave -> new ArrayList<>())
                    .add(new Trecho(destino, distanciaKm));
            grafo.computeIfAbsent(destino, chave -> new ArrayList<>())
                    .add(new Trecho(origem, distanciaKm));
        }
    }

    private Duration toleranciaTransporte(String componente, String modalidade) {
        String normalizado = Normalizer.normalize(componente == null ? "" : componente, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .toLowerCase(Locale.ROOT);
        long horas;
        if (normalizado.contains("hemacia")) {
            horas = REFRIGERADO.equals(modalidade) ? 24 : 2;
        } else if (normalizado.contains("plasma")) {
            horas = REFRIGERADO.equals(modalidade) ? 12 : 1;
        } else if (normalizado.contains("plaqueta")) {
            horas = REFRIGERADO.equals(modalidade) ? 6 : 1;
        } else {
            return null;
        }
        return Duration.ofHours(horas);
    }

    private String normalizarModalidade(String modalidade) {
        if (modalidade == null || modalidade.isBlank() || REFRIGERADO.equalsIgnoreCase(modalidade)) {
            return REFRIGERADO;
        }
        if (NAO_REFRIGERADO.equalsIgnoreCase(modalidade)) {
            return NAO_REFRIGERADO;
        }
        throw new IllegalArgumentException("Modalidade deve ser REFRIGERADO ou NAO_REFRIGERADO.");
    }

    private LocalDateTime limiteValidade(LocalDate validade) {
        return validade == null ? null : validade.atTime(LocalTime.MAX);
    }

    private PlanejamentoRota resultado(String status, String mensagem, String origem, String destino,
                                       String modalidade, List<String> caminho, double distancia,
                                       Duration duracao, Duration tolerancia, LocalDateTime chegada,
                                       LocalDateTime validade) {
        return new PlanejamentoRota(status, mensagem, origem, destino, modalidade, caminho, distancia,
                duracao, tolerancia, chegada, validade);
    }

    private record Trecho(String destino, double distanciaKm) {
    }

    private record No(String codigo, double distanciaKm) {
    }

    private record Caminho(List<String> hospitais, double distanciaKm) {
    }
}
