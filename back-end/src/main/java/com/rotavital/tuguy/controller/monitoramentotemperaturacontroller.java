package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.service.monitoramentotemperaturaservice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class monitoramentotemperaturacontroller {

    private final monitoramentotemperaturaservice monitoramentoService;

    public monitoramentotemperaturacontroller(monitoramentotemperaturaservice monitoramentoService) {
        this.monitoramentoService = monitoramentoService;
    }

    @GetMapping("/monitoramento/temperatura")
    public String listar(Model model) {
        model.addAttribute("bolsasMonitoradas", monitoramentoService.listarMonitoradas());
        return "monitoramento-temperatura";
    }
}
