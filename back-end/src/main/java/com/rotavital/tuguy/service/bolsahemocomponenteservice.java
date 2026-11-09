package com.rotavital.tuguy.service;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.repository.bolsahemocomponenterepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class bolsahemocomponenteservice {

    private final bolsahemocomponenterepository repository;
    private final CompatibilidadeABOService compatibilidadeABOService;
    private final bolsahemocomponentefefoservice fefoService;

    public bolsahemocomponenteservice(bolsahemocomponenterepository repository,
                                      CompatibilidadeABOService compatibilidadeABOService,
                                      bolsahemocomponentefefoservice fefoService) {
        this.repository = repository;
        this.compatibilidadeABOService = compatibilidadeABOService;
        this.fefoService = fefoService;
    }

    public bolsahemocomponente criar(bolsahemocomponente bolsa) {
        if (bolsa == null) {
            throw new IllegalArgumentException("Bolsa não pode ser nula");
        }

        if (bolsa.getCodigo() != null && !bolsa.getCodigo().isBlank()) {
            String codigoNormalizado = bolsa.getCodigo().trim();
            if (repository.existsByCodigoIgnoreCase(codigoNormalizado)) {
                throw new IllegalArgumentException("Já existe uma bolsa cadastrada com este código.");
            }
            bolsa.setCodigo(codigoNormalizado);
        }

        bolsa.setStatus(bolsa.getRequisicao() == null ? "DISPONIVEL" : "ALOCADA");
        bolsahemocomponente salva = repository.save(bolsa);
        if (salva.getCodigo() == null || salva.getCodigo().isBlank()) {
            salva.setCodigo("BOLSA-" + salva.getId());
            return repository.save(salva);
        }
        return salva;
    }

    public List<bolsahemocomponente> listarTodos() {
        return repository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<Long, String> codigosAlocadosPorRequisicao() {
        Map<Long, String> codigosPorRequisicao = new HashMap<>();
        repository.findAllByStatusIgnoreCase("ALOCADA").stream()
                .filter(bolsa -> bolsa.getRequisicao() != null)
                .sorted((primeira, segunda) -> Long.compare(segunda.getId(), primeira.getId()))
                .forEach(bolsa -> codigosPorRequisicao.putIfAbsent(
                        bolsa.getRequisicao().getId(), bolsa.getCodigo()));
        return codigosPorRequisicao;
    }

    public boolean existePorRequisicao(Long requisicaoId) {
        return repository.countByRequisicao_Id(requisicaoId) > 0;
    }

    public boolean temBolsaAlocada(Long requisicaoId) {
        return !repository.findAllByRequisicao_IdAndStatusIgnoreCase(requisicaoId, "ALOCADA").isEmpty();
    }

    public List<bolsahemocomponente> testeABO(String tipoRequisicao) {
        List<bolsahemocomponente> compativeis = repository.findAll().stream()
                .filter(bolsa -> compatibilidadeABOService.testeABO(
                        tipoRequisicao, bolsa.getTipoSanguineo()))
                .toList();
        return fefoService.listarEmOrdemFefo(compativeis);
    }

    @Transactional
    public bolsahemocomponente alocar(Long bolsaId, requisicao requisicao) {
        if (requisicao == null || requisicao.getId() == null) {
            throw new IllegalArgumentException("Requisição válida é obrigatória para alocar uma bolsa");
        }

        bolsahemocomponente bolsa = repository.findById(bolsaId)
                .orElseThrow(() -> new IllegalArgumentException("Bolsa não encontrada com o id: " + bolsaId));

        if (!bolsa.estaDisponivel()) {
            throw new IllegalStateException("A bolsa não está mais disponível para alocação");
        }

        if (!compatibilidadeABOService.testeABO(requisicao.getTipoSanguineo(), bolsa.getTipoSanguineo())) {
            throw new IllegalArgumentException("A bolsa não é compatível com o tipo sanguíneo da requisição");
        }

        List<bolsahemocomponente> alocacoesAnteriores =
                repository.findAllByRequisicao_IdAndStatusIgnoreCase(requisicao.getId(), "ALOCADA");
        for (bolsahemocomponente alocacaoAnterior : alocacoesAnteriores) {
            alocacaoAnterior.setRequisicao(null);
            alocacaoAnterior.setStatus(alocacaoAnterior.estaVencida() ? "VENCIDA" : "DISPONIVEL");
            repository.save(alocacaoAnterior);
        }

        bolsa.setRequisicao(requisicao);
        bolsa.setStatus("ALOCADA");
        return repository.save(bolsa);
    }

    public Optional<bolsahemocomponente> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public bolsahemocomponente atualizar(Long id, bolsahemocomponente bolsaAtualizada) {
        if (id == null || bolsaAtualizada == null) {
            throw new IllegalArgumentException("ID e bolsa são obrigatórios");
        }

        bolsahemocomponente existente = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Bolsa não encontrada para o ID informado"));
        bolsaAtualizada.setId(id);
        bolsaAtualizada.setCodigo(existente.getCodigo());
        bolsaAtualizada.setRequisicao(existente.getRequisicao());
        bolsaAtualizada.setStatus(existente.getRequisicao() == null ? "DISPONIVEL" : "ALOCADA");
        return repository.save(bolsaAtualizada);
    }

    public boolean deletar(Long id) {
        if (id == null) {
            return false;
        }

        if (!repository.existsById(id)) {
            return false;
        }

        repository.deleteById(id);
        return true;
    }

    public List<bolsahemocomponente> listarDisponiveis() {
        return repository.findAll().stream()
                .filter(bolsahemocomponente::estaDisponivel)
                .toList();
    }
}
