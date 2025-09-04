package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.service.BpmnEngineGeneratorService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Path;

@RestController
@RequestMapping("/generate")
public class GeneratorController {

    private final BpmnEngineGeneratorService generatorService;

    // Inyectando el servicio en el constructor
    public GeneratorController(BpmnEngineGeneratorService generatorService) {
        this.generatorService = generatorService;
    }

    // Endpoint para generar el proyecto Spring Boot desde la plantilla
    @GetMapping("/bpm-engine")
    public ResponseEntity<String> generateSpringBootProject() {
        try {
            // Llamar al servicio para generar el proyecto a partir de la plantilla
            Path projectPath = generatorService.generateProjectFromTemplate();

            return ResponseEntity.ok("Proyecto generado exitosamente en: " + projectPath.toAbsolutePath());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar el proyecto: " + e.getMessage());
        }
    }
}
