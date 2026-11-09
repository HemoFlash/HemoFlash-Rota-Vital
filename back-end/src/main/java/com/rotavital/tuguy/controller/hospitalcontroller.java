package com.rotavital.tuguy.controller;

import com.rotavital.tuguy.model.hospital;
import com.rotavital.tuguy.service.hospitalservice;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class hospitalcontroller {

    private final hospitalservice hospitalService;

    public hospitalcontroller(hospitalservice hospitalService) {
        this.hospitalService = hospitalService;
    }

    @GetMapping("/hospitais")
    public String listar(Model model) {
        model.addAttribute("hospitais", hospitalService.listarTodos());
        return "hospitais";
    }

    @GetMapping("/hospitais/novo")
    public String novo(Model model) {
        model.addAttribute("hospital", new hospital());
        return "novo-hospital";
    }

    @PostMapping("/hospitais")
    public String cadastrar(hospital hospital, Model model, RedirectAttributes redirectAttributes) {
        try {
            hospitalService.cadastrar(hospital);
            redirectAttributes.addFlashAttribute("sucessoHospital", "Hospital cadastrado com sucesso.");
            return "redirect:/hospitais";
        } catch (IllegalArgumentException e) {
            model.addAttribute("hospital", hospital);
            model.addAttribute("erroHospital", e.getMessage());
            return "novo-hospital";
        }
    }
}
