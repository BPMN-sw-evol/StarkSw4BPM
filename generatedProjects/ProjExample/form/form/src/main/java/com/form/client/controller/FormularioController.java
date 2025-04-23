package com.form.client.controller;

import com.form.client.dto.FormularioDTO;
import com.form.client.services.FormularioService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

import java.util.List;

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
        formularioService.guardarFormularioYIniciarProceso(formularioDTO);
        return "redirect:/formulario?exito";
    }

    @GetMapping("/lista")
    public String listarFormularios(Model model) {
        List<FormularioDTO> formularios = formularioService.obtenerTodos();
        model.addAttribute("formularios", formularios);
        return "lista";
    }

    @PostMapping("/aprobar/id/{id}")
    public String aprobarPorId(@PathVariable Long id) {
        formularioService.aprobarPorId(id);
        return "redirect:/formulario/lista";
    }



}