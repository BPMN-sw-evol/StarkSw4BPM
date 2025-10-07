package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.DtoGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.fileGenerator.external.generator.EntityGeneratorService;
import com.starksw4b.pmn.starksw4bpmn.model.FormFieldData;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpmn/test")
public class BpmnTestController {

    private final BpmnService bpmnService;
    private final BpmnParserService bpmnParserService;
    private final DtoGeneratorService dtoGeneratorService;
    private final EntityGeneratorService entityGeneratorService;

    public BpmnTestController(
            BpmnService bpmnService,
            BpmnParserService bpmnParserService,
            DtoGeneratorService dtoGeneratorService,
            EntityGeneratorService entityGeneratorService
    ) {
        this.bpmnService = bpmnService;
        this.bpmnParserService = bpmnParserService;
        this.dtoGeneratorService = dtoGeneratorService;
        this.entityGeneratorService = entityGeneratorService;
    }

    /**
     * 🧾 Leer campos de formulario (UserTasks con camunda:formData)
     */
    @GetMapping("/form-fields")
    public ResponseEntity<?> getUserFormFields(@RequestParam String fileName) {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + fileName;
            Map<String, List<FormFieldData>> campos = bpmnService.getEnrichedUserTaskFormFields(path);
            return ResponseEntity.ok(campos);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al leer los formularios del BPMN: " + e.getMessage());
        }
    }

    /**
     * ⚙️ Generar DTOs y Entities desde los formularios detectados
     */
    @GetMapping("/generate-classes")
    public ResponseEntity<?> generateDtoAndEntity(@RequestParam String fileName) {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + fileName;
            bpmnService.getEnrichedUserTaskFormFields(path);

            Path externalPath = Path.of(System.getProperty("user.dir")).resolve("cliente");

            dtoGeneratorService.generateDtos(externalPath);
            entityGeneratorService.generateEntities(externalPath);

            return ResponseEntity.ok("✅ DTOs y entidades generadas correctamente.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error generando clases: " + e.getMessage());
        }
    }

    /**
     * 🧩 Mostrar los elementos BPMN (incluye compuertas)
     */
    @GetMapping("/elements")
    public ResponseEntity<?> testBpmnElements(@RequestParam String fileName) {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + fileName;
            Map<String, List<Map<String, String>>> elementos = bpmnParserService.parseElements(path);

            System.out.println("\n📘 === ELEMENTOS DETECTADOS EN EL BPMN ===");
            for (Map.Entry<String, List<Map<String, String>>> entry : elementos.entrySet()) {
                System.out.println("\n🔹 Tipo: " + entry.getKey().toUpperCase());
                for (Map<String, String> e : entry.getValue()) {
                    System.out.println("   ➜ " + e.get("name") + "  (ID: " + e.get("key") + ")");
                }
            }
            System.out.println("==========================================\n");

            return ResponseEntity.ok("✅ Elementos BPMN listados en consola (ver logs del servidor).");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error al listar elementos BPMN: " + e.getMessage());
        }
    }
}
