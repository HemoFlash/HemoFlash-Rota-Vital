package com.rotavital.tuguy.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import com.rotavital.tuguy.model.user;
import com.rotavital.tuguy.service.bolsahemocomponenteservice;
import com.rotavital.tuguy.service.indicadoresredeservice;
import com.rotavital.tuguy.service.requisicaoservice;
import com.rotavital.tuguy.service.userservice;
import com.rotavital.tuguy.ordenacao.prioridadeutil;

import jakarta.validation.Valid;

@Controller 
public class usercontroller {

    private final userservice Service;
    private final requisicaoservice RequisicaoService;
    private final bolsahemocomponenteservice BolsaService;
    private final indicadoresredeservice IndicadoresService;

    // instância de ClienteService é injetada pelo Spring no controller
    public usercontroller(userservice service, requisicaoservice requisicaoService,
                          bolsahemocomponenteservice bolsaService,
                          indicadoresredeservice indicadoresService) {
        this.Service = service;
        this.RequisicaoService = requisicaoService;
        this.BolsaService = bolsaService;
        this.IndicadoresService = indicadoresService;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/login";
    }

    @GetMapping("/login")
    public String exibString(Model model) {
        // manda renderizar resources/templates/login.html
        // passando para esse html o model criado.
        return "login";
    }

    @PostMapping("/login")
    public String autenticar(@RequestParam String email, @RequestParam String senha, Model model){

        user usuario = Service.autenticar(email, senha);

        if (usuario != null){
            return "redirect:/menu";
        } 

        model.addAttribute("erro", "email ou senha invalidos");
        return "login";

    }


    @GetMapping("/menu")
    public String listar(Model model) {

        var requisicoes = RequisicaoService.listarTodos();
        var bolsas = BolsaService.listarTodos();
        model.addAttribute("users", Service.listarTodos());
        model.addAttribute("requisicoes", requisicoes.stream()
                .sorted(prioridadeutil.COMPARADOR)
                .toList());
        model.addAttribute("bolsas", bolsas);
        model.addAttribute("codigosAlocadosPorRequisicao", BolsaService.codigosAlocadosPorRequisicao());
        model.addAttribute("indicadores", IndicadoresService.calcular(bolsas, requisicoes));

        // manda renderizar resources/templates/login.html
        // passando para esse html o model criado.
        return "menu";
    }

    @PostMapping("/salvar")
    public String salvar(@Valid user user, BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("user", user);
            return "cadastrar";
        }
        try {
            Service.salvar(user);
        } catch (IllegalArgumentException e) {
            model.addAttribute("user", user);
            model.addAttribute("erroCadastro", e.getMessage());
            return "cadastrar";
        }
        return "redirect:/login";
    }
    
    // @PathVariable Long id --> Extrai o valor do ID da URL
    @GetMapping("/remover/{id}")
    public String remover(@PathVariable Long id) {
        Service.remover(id);
        return "redirect:/login";
    }
    

@GetMapping("/editar/{id}")
    public String editar(@PathVariable Long id, Model model) {
        model.addAttribute("user", Service.buscarPorId(id));
        return "cadastrar";
    }   

 @GetMapping("/cadastrar/")
    public String novo(Model model) {
        model.addAttribute("user", new user());
        return "cadastrar";
    }

}
