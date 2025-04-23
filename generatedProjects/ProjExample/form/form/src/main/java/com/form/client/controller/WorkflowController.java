package com.form.client.controller;

import com.form.client.dto.FormularioDTO;
import com.form.client.services.FormularioService;
import com.form.client.services.FormularioWorkflowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.ui.Model;

@RestController
@RequestMapping("/workflow")
public class WorkflowController {

    @Autowired
    private FormularioWorkflowService formularioWorkflowService;

    @PostMapping("/completar/{formularioId}")
    public ResponseEntity<String> completar(@PathVariable Long formularioId) {
        formularioWorkflowService.completarTareaFormulario(formularioId);
        return ResponseEntity.ok("Intento de completar tarea realizado.");
    }
}