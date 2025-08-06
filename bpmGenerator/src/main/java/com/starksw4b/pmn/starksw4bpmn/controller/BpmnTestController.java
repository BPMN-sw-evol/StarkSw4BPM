package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.DtoGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.EntityGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnTestController {

    private final BpmnService bpmnService;
    private final DtoGeneratorService dtoGeneratorService;
    private final EntityGeneratorService entityGeneratorService;


    public BpmnTestController(
            BpmnService bpmnService,
            DtoGeneratorService dtoGeneratorService,
            EntityGeneratorService entityGeneratorService
    ) {
        this.bpmnService = bpmnService;
        this.dtoGeneratorService = dtoGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
    }

    @GetMapping("/form-fields")
    public ResponseEntity<?> getUserFormFields(@RequestParam String fileName) {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + fileName;
            Map<String, List<FormFieldData>> campos = bpmnService.getEnrichedUserTaskFormFields(path);
            return ResponseEntity.ok(campos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al leer los formularios del BPMN: " + e.getMessage());
        }
    }

    @GetMapping("/generate-classes")
    public ResponseEntity<?> generateDtoAndEntity(@RequestParam String fileName) {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + fileName;
            Map<String, List<FormFieldData>> formFieldsMap = bpmnService.getEnrichedUserTaskFormFields(path);

            // Ruta del proyecto generado (cliente)
            Path externalPath = Path.of(System.getProperty("user.dir")).resolve("cliente");

            // Ejecutar generadores
            dtoGeneratorService.generateDtos(externalPath, formFieldsMap);
            entityGeneratorService.generateEntities(externalPath, formFieldsMap);

            return ResponseEntity.ok("✅ DTOs y entidades generadas correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error generando clases: " + e.getMessage());
        }
    }


}
