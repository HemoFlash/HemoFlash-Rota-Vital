package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.model.bolsahemocomponente;
import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.service.bolsahemocomponenteservice;
import com.rotavital.tuguy.service.requisicaoservice;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class requisicaocontroller {

    private final requisicaoservice Service;
    private final bolsahemocomponenteservice BolsaService;

    public requisicaocontroller(requisicaoservice service, bolsahemocomponenteservice bolsaService) {
        this.Service = service;
        this.BolsaService = bolsaService;
    }

    @GetMapping("/requisicoes")
    public String nova(requisicao requisicao) {
        return "requisicao";
    }

    @GetMapping("/requisicoes/{id}/editar")
    public String editarRequisicao(@PathVariable Long id, Model model) {
        model.addAttribute("requisicao", Service.buscarPorId(id));
        return "requisicao";
    }

    @GetMapping("/requisicoes/{requisicaoId}/alocar-bolsa")
    public String listarBolsasParaAlocacao(@PathVariable("requisicaoId") Long requisicaoId, Model model) {
        requisicao requisicao = Service.buscarPorId(requisicaoId);
        model.addAttribute("requisicao", requisicao);
        model.addAttribute("bolsas", BolsaService.testeABO(requisicao.getTipoSanguineo()));
        return "bolsas";
    }

    @PostMapping("/requisicoes/{requisicaoId}/alocar-bolsa/{bolsaId}")
    public String alocarBolsa(@PathVariable Long requisicaoId, @PathVariable Long bolsaId,
                              RedirectAttributes redirectAttributes) {
        requisicao requisicao = Service.buscarPorId(requisicaoId);
        try {
            BolsaService.alocar(bolsaId, requisicao);
            redirectAttributes.addFlashAttribute("sucessoAlocacao",
                    "Bolsa alocada para a requisição com sucesso.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirectAttributes.addFlashAttribute("erroAlocacao", e.getMessage());
        }

        return "redirect:/requisicoes/" + requisicaoId + "/alocar-bolsa";
    }

    @GetMapping("/bolsas")
    public String listarBolsas(Model model) {
        model.addAttribute("bolsas", BolsaService.listarTodos());
        return "bolsas";
    }

    @GetMapping("/bolsas/nova")
    public String novaBolsa(Model model) {
        bolsahemocomponente bolsa = new bolsahemocomponente();
        bolsa.setStatus("DISPONIVEL");
        model.addAttribute("bolsa", bolsa);
        return "bolsa-form";
    }

    @PostMapping("/bolsas")
    public String salvarBolsa(@Valid bolsahemocomponente bolsa, BindingResult result, Model model) {
        return salvarBolsaValidada(bolsa, result, model);
    }

    @PostMapping("/bolsas/{id}")
    public String atualizarBolsa(@PathVariable Long id, @Valid bolsahemocomponente bolsa,
                                 BindingResult result, Model model) {
        bolsa.setId(id);
        return salvarBolsaValidada(bolsa, result, model);
    }

    private String salvarBolsaValidada(bolsahemocomponente bolsa, BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "bolsa-form";
        }

        if (bolsa.getId() == null) {
            BolsaService.criar(bolsa);
        } else {
            bolsa.setRequisicao(BolsaService.buscarPorId(bolsa.getId())
                    .orElseThrow(() -> new RuntimeException("Bolsa não encontrada com o id: " + bolsa.getId()))
                    .getRequisicao());
            BolsaService.atualizar(bolsa.getId(), bolsa);
        }
        return "redirect:/menu";
    }

    @GetMapping("/bolsas/{id}/editar")
    public String editarBolsa(@PathVariable Long id, Model model) {
        model.addAttribute("bolsa", BolsaService.buscarPorId(id)
                .orElseThrow(() -> new RuntimeException("Bolsa não encontrada com o id: " + id)));
        return "bolsa-form";
    }

    @PostMapping("/bolsas/{id}/remover")
    public String removerBolsa(@PathVariable Long id) {
        BolsaService.deletar(id);
        return "redirect:/menu";
    }

    @PostMapping("/requisicoes")
    public String salvar(@Valid requisicao requisicao, BindingResult result) {
        if (result.hasErrors()) {
            return "requisicao";
        }

        Service.salvar(requisicao);
        return "redirect:/menu";
    }

    @PostMapping("/requisicoes/{id}")
    public String atualizarRequisicao(@PathVariable Long id, @Valid requisicao requisicao,
                                      BindingResult result) {
        requisicao.setId(id);
        if (result.hasErrors()) {
            return "requisicao";
        }

        Service.salvar(requisicao);
        return "redirect:/menu";
    }

    @PostMapping("/requisicoes/{id}/remover")
    public String removerRequisicao(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        if (BolsaService.existePorRequisicao(id)) {
            redirectAttributes.addFlashAttribute(
                    "erroRequisicao",
                    "Não é possível remover esta requisição porque existem bolsas associadas.");
            return "redirect:/menu";
        }

        Service.deletar(id);
        return "redirect:/menu";
    }
}