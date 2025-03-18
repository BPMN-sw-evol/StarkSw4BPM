package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.exception.InvalidBpmnException;
import com.starksw4b.pmn.starksw4bpmn.service.BpmnService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/bpmn")
public class BpmnUploadController {

    private final BpmnService bpmnService;

    public BpmnUploadController(BpmnService bpmnService) {
        this.bpmnService = bpmnService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> uploadBpmnFile(@RequestParam("file") MultipartFile file) {
        try {
            // Agregar logs para verificar el archivo
            System.out.println("Archivo recibido: " + file.getOriginalFilename());
            System.out.println("Tamaño del archivo: " + file.getSize() + " bytes");
            System.out.println("Tipo de contenido: " + file.getContentType());

            // Procesar el archivo
            bpmnService.processBpmnFile(file);

            return ResponseEntity.ok("Archivo BPMN procesado correctamente.");
        } catch (InvalidBpmnException e) {
            return ResponseEntity.badRequest().body("BPMN inválido: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error procesando el archivo BPMN.");
        }
    }
}

