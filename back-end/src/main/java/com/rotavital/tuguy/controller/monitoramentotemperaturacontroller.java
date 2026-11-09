package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.service.monitoramentotemperaturaservice;
import com.rotavital.tuguy.model.veiculotransporte;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class monitoramentotemperaturacontroller {

    private final monitoramentotemperaturaservice monitoramentoService;

    public monitoramentotemperaturacontroller(monitoramentotemperaturaservice monitoramentoService) {
        this.monitoramentoService = monitoramentoService;
    }

    @GetMapping("/monitoramento/temperatura")
    public String listar(Model model) {
        model.addAttribute("veiculosMonitorados", monitoramentoService.listarMonitorados());
        model.addAttribute("veiculo", new veiculotransporte());
        return "monitoramento-temperatura";
    }

    @PostMapping("/monitoramento/temperatura/veiculos")
    public String registrarVeiculo(veiculotransporte veiculo, Model model,
                                   RedirectAttributes redirectAttributes) {
        try {
            monitoramentoService.registrarVeiculoEmTransporte(veiculo);
            redirectAttributes.addFlashAttribute("sucessoVeiculo",
                    "Veículo registrado para monitoramento durante o transporte.");
            return "redirect:/monitoramento/temperatura";
        } catch (IllegalArgumentException e) {
            model.addAttribute("veiculosMonitorados", monitoramentoService.listarMonitorados());
            model.addAttribute("veiculo", veiculo);
            model.addAttribute("erroVeiculo", e.getMessage());
            return "monitoramento-temperatura";
        }
    }

    @PostMapping("/monitoramento/temperatura/veiculos/{id}/concluir")
    public String concluirTransporte(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        monitoramentoService.concluirTransporte(id);
        redirectAttributes.addFlashAttribute("sucessoVeiculo", "Transporte concluído.");
        return "redirect:/monitoramento/temperatura";
    }
}
