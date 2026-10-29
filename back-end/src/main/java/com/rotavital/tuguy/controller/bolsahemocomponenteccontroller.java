package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.service.bolsahemocomponenteservice;
import com.rotavital.tuguy.service.bolsahemocomponentefefoservice;
import com.rotavital.tuguy.service.requisicaoservice;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bolsas")
public class bolsahemocomponenteccontroller {

    private final bolsahemocomponenteservice service;
    private final bolsahemocomponentefefoservice fefoService;
    private final requisicaoservice requisicaoService;

    public bolsahemocomponenteccontroller(bolsahemocomponenteservice service,
                                          bolsahemocomponentefefoservice fefoService,
                                          requisicaoservice requisicaoService) {
        this.service = service;
        this.fefoService = fefoService;
        this.requisicaoService = requisicaoService;
    }

    @PostMapping
    public ResponseEntity<?> criar(@Valid @RequestBody bolsahemocomponente bolsa) {
        try {
            if (bolsa.getRequisicao() == null || bolsa.getRequisicao().getId() == null) {
                return ResponseEntity.badRequest().body("Requisição obrigatória para cadastro da bolsa.");
            }

            requisicao requisicaoExistente = requisicaoService.buscarPorId(bolsa.getRequisicao().getId());
            bolsa.setRequisicao(requisicaoExistente);

            if (bolsa.getStatus() == null || bolsa.getStatus().isBlank()) {
                bolsa.setStatus("DISPONIVEL");
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(service.criar(bolsa));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public List<bolsahemocomponente> listarTodos() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    public ResponseEntity<bolsahemocomponente> buscarPorId(@PathVariable Long id) {
        return service.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/fefo")
    public List<bolsahemocomponente> listarFefo() {
        return fefoService.listarEmOrdemFefo(service.listarTodos());
    }

    @GetMapping("/requisicao/{requisicaoId}")
    public List<bolsahemocomponente> listarPorRequisicao(@PathVariable Long requisicaoId) {
        return service.listarTodos().stream()
                .filter(b -> b.getRequisicao() != null && requisicaoId.equals(b.getRequisicao().getId()))
                .toList();
    }

    @PutMapping("/{id}")
    public ResponseEntity<bolsahemocomponente> atualizar(@PathVariable Long id,
                                                         @Valid @RequestBody bolsahemocomponente bolsa) {
        if (bolsa.getRequisicao() != null && bolsa.getRequisicao().getId() != null) {
            requisicao requisicaoExistente = requisicaoService.buscarPorId(bolsa.getRequisicao().getId());
            bolsa.setRequisicao(requisicaoExistente);
        }

        return ResponseEntity.ok(service.atualizar(id, bolsa));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        boolean removido = service.deletar(id);
        return removido ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
