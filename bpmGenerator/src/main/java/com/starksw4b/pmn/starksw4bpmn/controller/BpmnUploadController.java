package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnUploadController {

    private final BpmnService bpmnService;
    private static String nombreArchivoBpmn = "proyecto.bpmn";

    public BpmnUploadController(BpmnService bpmnService) {
        this.bpmnService = bpmnService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadBpmnFile(@RequestParam("file") MultipartFile file) {
        try {
            System.out.println("Archivo recibido: " + file.getOriginalFilename());

            nombreArchivoBpmn = bpmnService.processBpmnFile(file); // guardar nombre original

            return ResponseEntity.ok("Archivo BPMN procesado correctamente.");
        } catch (InvalidBpmnException e) {
            return ResponseEntity.badRequest().body("BPMN inválido: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando el archivo BPMN.");
        }
    }

    @GetMapping("/tasks")
    public ResponseEntity<Map<String, List<String>>> getBpmnTasks() {
        try {
            String path = "uploads/" + nombreArchivoBpmn;
            Map<String, List<String>> tareas = bpmnService.getTareasPorTipo(path);
            return ResponseEntity.ok(tareas);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

}

