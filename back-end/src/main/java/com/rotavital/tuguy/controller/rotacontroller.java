package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.model.requisicao;
import com.rotavital.tuguy.service.PlanejamentoRotaService;
import com.rotavital.tuguy.service.requisicaoservice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class rotacontroller {

    private final requisicaoservice requisicaoService;
    private final PlanejamentoRotaService planejamentoService;

    public rotacontroller(requisicaoservice requisicaoService,
                          PlanejamentoRotaService planejamentoService) {
        this.requisicaoService = requisicaoService;
        this.planejamentoService = planejamentoService;
    }

    @GetMapping("/requisicoes/{id}/rota")
    public String planejar(@PathVariable Long id,
                           @RequestParam(defaultValue = "REFRIGERADO") String modalidade,
                           Model model) {
        requisicao requisicao = requisicaoService.buscarPorId(id);
        model.addAttribute("requisicao", requisicao);
        model.addAttribute("modalidadeSelecionada", modalidade);
        model.addAttribute("resultado", planejamentoService.planejar(requisicao, modalidade));
        return "rota";
    }
}
