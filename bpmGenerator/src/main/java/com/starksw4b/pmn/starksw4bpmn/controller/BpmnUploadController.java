package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnParserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnUploadController {

    private final BpmnService bpmnService;
    private final BpmnParserService bpmnParserService;
    private static String nombreArchivoBpmn = "proyecto.bpmn";

    public BpmnUploadController(BpmnService bpmnService, BpmnParserService bpmnParserService) {
        this.bpmnService = bpmnService;
        this.bpmnParserService = bpmnParserService;
    }

    /**
     * 📥 Subir archivo BPMN, guardarlo y registrar automáticamente
     * las tareas y compuertas en la base de datos.
     */
    @PostMapping("/upload")
    public ResponseEntity<String> uploadBpmnFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Archivo recibido: " + file.getOriginalFilename());

            nombreArchivoBpmn = bpmnService.processBpmnFile(file); // guarda y procesa
            return ResponseEntity.ok("✅ Archivo BPMN procesado correctamente.");
        } catch (InvalidBpmnException e) {
            return ResponseEntity.badRequest().body("⚠️ BPMN inválido: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("❌ Error procesando el archivo BPMN: " + e.getMessage());
        }
    }

    /**
     * 🔍 Obtener todas las tareas detectadas (incluye compuertas)
     */
    @GetMapping("/tasks")
    public ResponseEntity<Map<String, List<Map<String, String>>>> getBpmnTasks() {
        try {
            String path = System.getProperty("user.dir") + "/uploads/" + nombreArchivoBpmn;
            Map<String, List<Map<String, String>>> tareas = bpmnParserService.parseElements(path);
            return ResponseEntity.ok(tareas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", List.of(Map.of("message", e.getMessage()))));
        }
    }
}
