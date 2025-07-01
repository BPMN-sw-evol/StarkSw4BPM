package com.starksw4b.pmn.starksw4bpmn.controller;

import com.starksw4b.pmn.starksw4bpmn.facade.SystemGeneratorFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.core.io.Resource;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/generator")
public class JavaDelegateGeneratorController {

    private final SystemGeneratorFacade generatorFacade;

    @Autowired
    public JavaDelegateGeneratorController(SystemGeneratorFacade generatorFacade) {
        this.generatorFacade = generatorFacade;
    }

    @PostMapping("/generate-delegate")
    public String generateDelegateClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseJavaDelegate(className);
            return "Clase JavaDelegate generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase JavaDelegate: " + e.getMessage();
        }
    }

    @PostMapping("/generate-delegate-expression")
    public String generateDelegateExpressionClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseDelegateExpression(className);
            return "Clase DelegateExpression generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase DelegateExpression: " + e.getMessage();
        }
    }

    @PostMapping("/generate-send-task")
    public String generateSendTaskClass(@RequestParam String className) {
        try {
            generatorFacade.generarClaseSendTask(className);
            return "Clase SendTask generada correctamente: " + className + ".java";
        } catch (IOException e) {
            return "Error al generar la clase SendTask: " + e.getMessage();
        }
    }

    @PostMapping("/generate-from-config")
    public ResponseEntity<String> generateFromConfig() {
        try {
            generatorFacade.generarClasesDesdeConfiguracion();
            return ResponseEntity.ok("Clases generadas desde configuración.");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al generar clases desde configuración: " + e.getMessage());
        }
    }

    @GetMapping("/download-zip")
    public ResponseEntity<Resource> downloadGeneratedProjectZip() {
        try {
            // Ruta del proyecto generado (ajústala según tu estructura)
            Path projectPath = Paths.get("../generatedProjects/generatedProject/BPM-Engine");
            Path zipPath = Files.createTempFile("project-", ".zip");

            // Comprimir carpeta
            try (FileOutputStream fos = new FileOutputStream(zipPath.toFile());
                 ZipOutputStream zos = new ZipOutputStream(fos)) {

                Files.walk(projectPath)
                        .filter(path -> !Files.isDirectory(path))
                        .forEach(path -> {
                            try {
                                ZipEntry zipEntry = new ZipEntry(projectPath.relativize(path).toString());
                                zos.putNextEntry(zipEntry);
                                Files.copy(path, zos);
                                zos.closeEntry();
                            } catch (IOException e) {
                                throw new RuntimeException("Error al comprimir archivo: " + path, e);
                            }
                        });
            }

            InputStreamResource resource = new InputStreamResource(new FileInputStream(zipPath.toFile()));

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=proyecto-generado.zip")
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(null);
        }
    }
}
