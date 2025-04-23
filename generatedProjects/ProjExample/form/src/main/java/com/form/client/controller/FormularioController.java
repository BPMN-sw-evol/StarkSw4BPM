package com.form.client.controller;

import com.form.client.dto.FormularioDTO;
import com.form.client.services.FormularioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@Controller
@RequestMapping("/formulario")
public class FormularioController {

    private final FormularioService formularioService;

    public FormularioController(FormularioService formularioService) {
        this.formularioService = formularioService;
    }

    @GetMapping
    public String mostrarFormulario(Model model) {
        model.addAttribute("formulario", new FormularioDTO());
        return "formulario";
    }

    @PostMapping
    public String enviarFormulario(@ModelAttribute("formulario") FormularioDTO formularioDTO) {
        formularioService.guardarFormulario(formularioDTO);
        return "redirect:/formulario?exito";
    }
}